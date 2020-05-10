package custom.jmeter.grpc.core;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import custom.jmeter.grpc.core.grpc.ChannelFactory;
import custom.jmeter.grpc.core.grpc.DynamicGrpcClient;
import custom.jmeter.grpc.core.io.MessageReader;
import custom.jmeter.grpc.core.protobuf.ProtoMethodName;
import custom.jmeter.grpc.core.protobuf.ProtocInvoker;
import custom.jmeter.grpc.core.protobuf.ServiceResolver;
import io.grpc.CallOptions;
import io.grpc.Channel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class ClientCaller {
    private Descriptors.MethodDescriptor methodDescriptor;
    private JsonFormat.TypeRegistry registry;
    private HostAndPort hostAndPort;
    private DynamicGrpcClient dynamicClient;
    private ImmutableList<DynamicMessage> requestMessages;

    public ClientCaller(String HOST_PORT, String TEST_PROTO_FILES, String FULL_METHOD) {
        this.init(HOST_PORT, TEST_PROTO_FILES, FULL_METHOD);
    }

    private void init(String HOST_PORT, String TEST_PROTO_FILES, String FULL_METHOD) {
        hostAndPort = HostAndPort.fromString(HOST_PORT);
        ProtoMethodName grpcMethodName =
                ProtoMethodName.parseFullGrpcMethodName(FULL_METHOD);

        ChannelFactory channelFactory = ChannelFactory.create();

        Channel channel;
        channel = channelFactory.createChannel(hostAndPort);

        // Fetch the appropriate file descriptors for the service.
        final DescriptorProtos.FileDescriptorSet fileDescriptorSet;

        try {
            fileDescriptorSet = ProtocInvoker.forConfig(TEST_PROTO_FILES).invoke();
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

    public String buildRequest(String pathReq, String jsonData) {
        Path REQUEST_FILE = Paths.get(pathReq);

        requestMessages =
                MessageReader.forFile(REQUEST_FILE, methodDescriptor.getInputType(), registry, jsonData).read();
        return requestMessages.get(0).toString();
    }

    public DynamicMessage call(long deadlineMs) {
        DynamicMessage resp;
        try {
            resp = dynamicClient.blockingUnaryCall(requestMessages, callOptions(deadlineMs));
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

}
