package vn.zalopay.benchmark.core.grpc;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.DynamicMessage;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor.MethodType;
import io.grpc.stub.ClientCalls;

public class DynamicGrpcClient {
    private final MethodDescriptor protoMethodDescriptor;
    private final Channel channel;

    public static DynamicGrpcClient create(MethodDescriptor protoMethod, Channel channel) {
        return new DynamicGrpcClient(protoMethod, channel);
    }

    @VisibleForTesting
    DynamicGrpcClient(MethodDescriptor protoMethodDescriptor, Channel channel) {
        this.protoMethodDescriptor = protoMethodDescriptor;
        this.channel = channel;
    }

    public DynamicMessage blockingUnaryCall(
            ImmutableList<DynamicMessage> requests,
            CallOptions callOptions) {
        return ClientCalls.blockingUnaryCall(
                this.channel, createGrpcMethodDescriptor(), callOptions, requests.get(0)
        );
    }

    private io.grpc.MethodDescriptor<DynamicMessage, DynamicMessage> createGrpcMethodDescriptor() {
        return io.grpc.MethodDescriptor.<DynamicMessage, DynamicMessage>create(
                getMethodType(),
                getFullMethodName(),
                new DynamicMessageMarshaller(protoMethodDescriptor.getInputType()),
                new DynamicMessageMarshaller(protoMethodDescriptor.getOutputType()));
    }

    private String getFullMethodName() {
        String serviceName = protoMethodDescriptor.getService().getFullName();
        String methodName = protoMethodDescriptor.getName();
        return io.grpc.MethodDescriptor.generateFullMethodName(serviceName, methodName);
    }

    private MethodType getMethodType() {
        boolean clientStreaming = protoMethodDescriptor.toProto().getClientStreaming();
        boolean serverStreaming = protoMethodDescriptor.toProto().getServerStreaming();

        if (!clientStreaming && !serverStreaming) {
            return MethodType.UNARY;
        } else if (!clientStreaming && serverStreaming) {
            return MethodType.SERVER_STREAMING;
        } else if (clientStreaming && !serverStreaming) {
            return MethodType.CLIENT_STREAMING;
        }

        return MethodType.BIDI_STREAMING;
    }
}
