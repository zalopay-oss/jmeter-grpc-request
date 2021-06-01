package vn.zalopay.benchmark.core.grpc;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.DynamicMessage;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor.MethodType;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;
import vn.zalopay.benchmark.core.channel.ComponentObserver;
import vn.zalopay.benchmark.core.channel.DoneObserver;

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

    public ListenableFuture<Void> blockingUnaryCall(
            ImmutableList<DynamicMessage> requests,
            StreamObserver<DynamicMessage> responseObserver,
            CallOptions callOptions) {
        DoneObserver<DynamicMessage> doneObserver = new DoneObserver<>();
        ClientCalls.asyncUnaryCall(this.channel.newCall(createGrpcMethodDescriptor(), callOptions), requests.get(0), ComponentObserver.of(responseObserver, doneObserver));
        return doneObserver.getCompletionFuture();
    }

    public ListenableFuture<Void> callServerStreaming(ImmutableList<DynamicMessage> requests,
                                                      StreamObserver<DynamicMessage> responseObserver, CallOptions callOptions) {
        long numRequests = requests.size();
        DoneObserver<DynamicMessage> doneObserver = new DoneObserver<>();
        ClientCalls.asyncServerStreamingCall(this.channel.newCall(createGrpcMethodDescriptor(), callOptions), requests.get(0),
                ComponentObserver.of(responseObserver, doneObserver));
        return doneObserver.getCompletionFuture();
    }

    public ListenableFuture<Void> callClientStreaming(ImmutableList<DynamicMessage> requests,
                                                      StreamObserver<DynamicMessage> responseObserver, CallOptions callOptions) {
        DoneObserver<DynamicMessage> doneObserver = new DoneObserver<>();
        StreamObserver<DynamicMessage> requestObserver = ClientCalls.asyncClientStreamingCall(
                this.channel.newCall(createGrpcMethodDescriptor(), callOptions), ComponentObserver.of(responseObserver, doneObserver));
        requests.forEach(requestObserver::onNext);
        requestObserver.onCompleted();
        return doneObserver.getCompletionFuture();
    }

    public ListenableFuture<Void> callBidiStreaming(ImmutableList<DynamicMessage> requests,
                                                    StreamObserver<DynamicMessage> responseObserver, CallOptions callOptions) {
        DoneObserver<DynamicMessage> doneObserver = new DoneObserver<>();
        StreamObserver<DynamicMessage> requestObserver = ClientCalls.asyncBidiStreamingCall(
                this.channel.newCall(createGrpcMethodDescriptor(), callOptions), ComponentObserver.of(responseObserver, doneObserver));
        requests.forEach(requestObserver::onNext);
        requestObserver.onCompleted();
        return doneObserver.getCompletionFuture();
    }

    private io.grpc.MethodDescriptor<DynamicMessage, DynamicMessage> createGrpcMethodDescriptor() {
        return io.grpc.MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder()
                .setFullMethodName(getFullMethodName())
                .setType(getMethodType())
                .setResponseMarshaller(new DynamicMessageMarshaller(protoMethodDescriptor.getOutputType()))
                .setRequestMarshaller(new DynamicMessageMarshaller(protoMethodDescriptor.getInputType())).build();
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
