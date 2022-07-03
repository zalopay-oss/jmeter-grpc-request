package vn.zalopay.benchmark.core;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import vn.zalopay.benchmark.core.channel.ComponentObserver;
import vn.zalopay.benchmark.core.config.GrpcRequestConfig;
import vn.zalopay.benchmark.core.grpc.ChannelFactory;
import vn.zalopay.benchmark.core.grpc.DynamicGrpcClient;
import vn.zalopay.benchmark.core.message.Reader;
import vn.zalopay.benchmark.core.message.Writer;
import vn.zalopay.benchmark.core.protobuf.ProtoMethodName;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.core.protobuf.ServiceResolver;
import vn.zalopay.benchmark.core.specification.GrpcResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ClientCaller {
    private Descriptors.MethodDescriptor methodDescriptor;
    private JsonFormat.TypeRegistry registry;
    private DynamicGrpcClient dynamicClient;
    private ImmutableList<DynamicMessage> requestMessages;
    private ManagedChannel channel;
    private HostAndPort hostAndPort;
    private Map<String, String> metadataMap;
    private boolean tls;
    private boolean disableTtlVerification;
    private int awaitTerminationTimeout;
    private GrpcRequestConfig requestConfig;
    ChannelFactory channelFactory;

    public ClientCaller(GrpcRequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        this.init(requestConfig.getHostPort(), requestConfig.getProtoFolder(), requestConfig.getLibFolder(),
                requestConfig.getFullMethod(), requestConfig.isTls(), requestConfig.isTlsDisableVerification(),
                requestConfig.getAwaitTerminationTimeout());
    }

    private void init(String hostPort, String testProtoFiles, String libFolder, String fullMethod, boolean tls,
                      boolean tlsDisableVerification, int awaitTerminationTimeout) {
        try {
            this.awaitTerminationTimeout = awaitTerminationTimeout;
            this.tls = tls;
            disableTtlVerification = tlsDisableVerification;
            hostAndPort = HostAndPort.fromString(hostPort);
            metadataMap = new LinkedHashMap<>();
            channelFactory = ChannelFactory.create();
            ProtoMethodName grpcMethodName =
                    ProtoMethodName.parseFullGrpcMethodName(fullMethod);

            // Fetch the appropriate file descriptors for the service.
            final DescriptorProtos.FileDescriptorSet fileDescriptorSet;

            try {
                fileDescriptorSet = ProtocInvoker.forConfig(testProtoFiles, libFolder).invoke();
            } catch (Exception e) {
                shutdownNettyChannel();
                throw new RuntimeException("Unable to resolve service by invoking protoc", e);
            }

            // Set up the dynamic client and make the call.
            ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
            methodDescriptor = serviceResolver.resolveServiceMethod(grpcMethodName);

            createDynamicClient();

            // This collects all known types into a registry for resolution of potential "Any" types.
            registry = JsonFormat.TypeRegistry.newBuilder()
                    .add(serviceResolver.listMessageTypes())
                    .build();
        } catch (Throwable t) {
            shutdownNettyChannel();
            throw t;
        }
    }

    private Map<String, String> buildHashMetadata(String metadata) {
        Map<String, String> metadataHash = new LinkedHashMap<>();

        if (Strings.isNullOrEmpty(metadata)) {
            return metadataHash;
        }

        if (metadata.startsWith("{") && metadata.endsWith("}")) {
            try {
                Map<String, Object> map = JSONObject.parseObject(metadata);
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    metadataHash.put(entry.getKey(), (String) entry.getValue());
                }
            } catch (Exception e) {
                Preconditions.checkArgument(1 == 2,
                        "Metadata entry must be valid JSON String or in key1:value1,key2:value2 format if not JsonString but found: " + metadata);
            }
        } else {
            String[] keyValue;
            for (String part : metadata.split(",")) {
                keyValue = part.split(":", 2);
                Preconditions.checkArgument(keyValue.length == 2,
                        "Metadata entry must be valid JSON String or in key1:value1,key2:value2 format if not JsonString but found: " + metadata);
                String value = keyValue[1];
                try {
                    value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException ignored) {
                }

                metadataHash.put(keyValue[0], value);
            }
        }

        return metadataHash;
    }

    public void createDynamicClient() {
        channel = channelFactory.createChannel(hostAndPort, tls, disableTtlVerification, metadataMap,
                requestConfig.getMaxInboundMessageSize(), requestConfig.getMaxInboundMetadataSize());
        dynamicClient = DynamicGrpcClient.create(methodDescriptor, channel);
    }

    public boolean isShutdown() {
        return channel.isShutdown();
    }

    public boolean isTerminated() {
        return channel.isTerminated();
    }

    public String buildRequestAndMetadata(String jsonData, String metadata) {
        try {
            metadataMap.clear();
            metadataMap.putAll(buildHashMetadata(metadata));
            requestMessages = Reader.create(methodDescriptor.getInputType(), jsonData, registry).read();
            return JsonFormat.printer().includingDefaultValueFields().usingTypeRegistry(registry).print(requestMessages.get(0));
        } catch (IllegalArgumentException e) {
            shutdownNettyChannel();
            throw e;
        } catch (Exception e) {
            shutdownNettyChannel();
            throw new RuntimeException("Caught exception while parsing request for rpc", e);
        }
    }

    public GrpcResponse call(String deadlineMs) {
        long deadline = parsingDeadlineTime(deadlineMs);
        GrpcResponse output = new GrpcResponse();
        StreamObserver<DynamicMessage> streamObserver = ComponentObserver.of(Writer.create(output, registry));
        try {
            dynamicClient.blockingUnaryCall(requestMessages, streamObserver, callOptions(deadline)).get();
        } catch (Exception e) {
            shutdownNettyChannel();
            throw new RuntimeException(String.format("Caught exception while waiting for rpc %s",
                    getDetailedErrorSendGRPC(e)), e);
        }
        return output;
    }

    public GrpcResponse callServerStreaming(String deadlineMs) {
        long deadline = parsingDeadlineTime(deadlineMs);
        GrpcResponse output = new GrpcResponse();
        StreamObserver<DynamicMessage> streamObserver = ComponentObserver.of(Writer.create(output, registry));
        try {
            dynamicClient.callServerStreaming(requestMessages, streamObserver, callOptions(deadline)).get();
        } catch (Exception e) {
            shutdownNettyChannel();
            throw new RuntimeException(String.format("Caught exception while waiting for rpc %s",
                    getDetailedErrorSendGRPC(e)), e);
        }
        return output;
    }

    public GrpcResponse callClientStreaming(String deadlineMs) {
        long deadline = parsingDeadlineTime(deadlineMs);
        GrpcResponse output = new GrpcResponse();
        StreamObserver<DynamicMessage> streamObserver = ComponentObserver.of(Writer.create(output, registry));
        try {
            dynamicClient.callClientStreaming(requestMessages, streamObserver, callOptions(deadline)).get();
        } catch (Exception e) {
            shutdownNettyChannel();
            throw new RuntimeException(String.format("Caught exception while waiting for rpc %s",
                    getDetailedErrorSendGRPC(e)), e);
        }
        return output;
    }

    public GrpcResponse callBidiStreaming(String deadlineMs) {
        long deadline = parsingDeadlineTime(deadlineMs);
        GrpcResponse output = new GrpcResponse();
        StreamObserver<DynamicMessage> streamObserver = ComponentObserver.of(Writer.create(output, registry));
        try {
            dynamicClient.callBidiStreaming(requestMessages, streamObserver, callOptions(deadline)).get();
        } catch (Exception e) {
            shutdownNettyChannel();
            throw new RuntimeException(String.format("Caught exception while waiting for rpc %s",
                    getDetailedErrorSendGRPC(e)), e);
        }
        return output;
    }

    private static CallOptions callOptions(long deadlineMs) {
        CallOptions result = CallOptions.DEFAULT;
        if (deadlineMs > 0) {
            result = result.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS);
        }
        return result;
    }

    public void shutdownNettyChannel() {
        try {
            if (channel != null) {
                channel.shutdown();
                channel.awaitTermination(awaitTerminationTimeout, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Caught exception while shutting down channel", e);
        }
    }

    private Long parsingDeadlineTime(String deadlineMs) {
        try {
            return Long.parseLong(deadlineMs);
        } catch (Exception e) {
            throw new RuntimeException("Caught exception while parsing deadline to long", e);
        }
    }

    public String getMetadataString() {
        return metadataMap.entrySet()
                .stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
    }

    private String getDetailedErrorSendGRPC(Exception e) {
        StringBuilder sb = new StringBuilder();
        Throwable t = e.getCause();
        while (t != null) {
            sb.append("\n");
            sb.append(t.toString() + ".");
            sb.append("\n");
            t = t.getCause();
        }
        return sb.toString();
    }
}
