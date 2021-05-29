package vn.zalopay.benchmark.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import vn.zalopay.benchmark.core.grpc.ChannelFactory;
import vn.zalopay.benchmark.core.grpc.DynamicGrpcClient;
import vn.zalopay.benchmark.core.io.MessageReader;
import vn.zalopay.benchmark.core.protobuf.ProtoMethodName;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.core.protobuf.ServiceResolver;

public class ClientCaller {
    private Descriptors.MethodDescriptor methodDescriptor;
    private JsonFormat.TypeRegistry registry;
    private DynamicGrpcClient dynamicClient;
    private ImmutableList<DynamicMessage> requestMessages;
    private ManagedChannel channel;

    public ClientCaller(String HOST_PORT, String TEST_PROTO_FILES, String LIB_FOLDER, String FULL_METHOD, boolean TLS, boolean TLS_DISABLE_VERIFICATION, String METADATA) {
        this.init(HOST_PORT, TEST_PROTO_FILES, LIB_FOLDER, FULL_METHOD, TLS, TLS_DISABLE_VERIFICATION, METADATA);
    }

    private void init(String HOST_PORT, String TEST_PROTO_FILES, String LIB_FOLDER, String FULL_METHOD, boolean tls, boolean tlsDisableVerification, String metadata) {
        HostAndPort hostAndPort = HostAndPort.fromString(HOST_PORT);
        ProtoMethodName grpcMethodName =
                ProtoMethodName.parseFullGrpcMethodName(FULL_METHOD);

        ChannelFactory channelFactory = ChannelFactory.create();
        Map<String, String> metadataMap = buildHashMetadata(metadata);
        try {
            channel = channelFactory.createChannel(hostAndPort, tls, tlsDisableVerification, metadataMap);
        }catch (IllegalStateException e){
            throw new RuntimeException("Unable to create channel grpc by invoking tls", e);
        }
        
        // Fetch the appropriate file descriptors for the service.
        final DescriptorProtos.FileDescriptorSet fileDescriptorSet;

        try {
            fileDescriptorSet = ProtocInvoker.forConfig(TEST_PROTO_FILES, LIB_FOLDER).invoke();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to resolve service by invoking protoc", t);
        }

        // Set up the dynamic client and make the call.
        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
        methodDescriptor = serviceResolver.resolveServiceMethod(grpcMethodName);

        dynamicClient = DynamicGrpcClient.create(methodDescriptor, channel);

        // This collects all known types into a registry for resolution of potential "Any" types.
        registry = JsonFormat.TypeRegistry.newBuilder()
                .add(serviceResolver.listMessageTypes())
                .build();
    }

    private Map<String, String> buildHashMetadata(String metadata) {
        Map<String, String> metadataHash = new LinkedHashMap<>();

        if(Strings.isNullOrEmpty(metadata))
            return metadataHash;

        String[] keyValue;
        for (String part : metadata.split(",")){
            keyValue = part.split(":", 2);

            Preconditions.checkArgument(keyValue.length == 2,
                "Metadata entry must be defined in key1:value1,key2:value2 format: " + metadata);

            metadataHash.put(keyValue[0], keyValue[1]);
        }

        return metadataHash;
    }

    public String buildRequest(String jsonData) {
        requestMessages =
                MessageReader.forJSON(methodDescriptor.getInputType(), registry, jsonData).read();

        try {
            return JsonFormat.printer().print(requestMessages.get(0));
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Caught exception while parsing request for rpc", e);
        }
    }

    public DynamicMessage call(String deadlineMs) {
        long deadline;
        try {
            deadline = Long.parseLong(deadlineMs);
        }catch (Exception e){
            throw new RuntimeException("Caught exception while parsing deadline to long", e);
        }

        DynamicMessage resp;
        try {
            resp = dynamicClient.blockingUnaryCall(requestMessages, callOptions(deadline));
        } catch (Throwable t) {
            throw new RuntimeException("Caught exception while waiting for rpc", t);
        }
        return resp;
    }

    private static CallOptions callOptions(long deadlineMs) {
        CallOptions result = CallOptions.DEFAULT;
        if (deadlineMs > 0) {
            result = result.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS);
        }
        return result;
    }

    public void shutdown(){

        try {
            if (channel != null)
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Caught exception while shutting down channel", e);
        }
    }

}
