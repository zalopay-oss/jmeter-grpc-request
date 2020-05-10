package generated.xn.services.ex.api.grpcstream.protos;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.18.0)",
    comments = "Source: segment.proto")
public final class SegmentServicesGrpc {

  private SegmentServicesGrpc() {}

  public static final String SERVICE_NAME = "data_services_seg.SegmentServices";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<generated.xn.services.ex.api.grpcstream.protos.SegmentReq,
      generated.xn.services.ex.api.grpcstream.protos.SegmentResp> getCheckSegMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "checkSeg",
      requestType = generated.xn.services.ex.api.grpcstream.protos.SegmentReq.class,
      responseType = generated.xn.services.ex.api.grpcstream.protos.SegmentResp.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<generated.xn.services.ex.api.grpcstream.protos.SegmentReq,
      generated.xn.services.ex.api.grpcstream.protos.SegmentResp> getCheckSegMethod() {
    io.grpc.MethodDescriptor<generated.xn.services.ex.api.grpcstream.protos.SegmentReq, generated.xn.services.ex.api.grpcstream.protos.SegmentResp> getCheckSegMethod;
    if ((getCheckSegMethod = SegmentServicesGrpc.getCheckSegMethod) == null) {
      synchronized (SegmentServicesGrpc.class) {
        if ((getCheckSegMethod = SegmentServicesGrpc.getCheckSegMethod) == null) {
          SegmentServicesGrpc.getCheckSegMethod = getCheckSegMethod = 
              io.grpc.MethodDescriptor.<generated.xn.services.ex.api.grpcstream.protos.SegmentReq, generated.xn.services.ex.api.grpcstream.protos.SegmentResp>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "data_services_seg.SegmentServices", "checkSeg"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.xn.services.ex.api.grpcstream.protos.SegmentReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.xn.services.ex.api.grpcstream.protos.SegmentResp.getDefaultInstance()))
                  .setSchemaDescriptor(new SegmentServicesMethodDescriptorSupplier("checkSeg"))
                  .build();
          }
        }
     }
     return getCheckSegMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SegmentServicesStub newStub(io.grpc.Channel channel) {
    return new SegmentServicesStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SegmentServicesBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SegmentServicesBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SegmentServicesFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SegmentServicesFutureStub(channel);
  }

  /**
   */
  public static abstract class SegmentServicesImplBase implements io.grpc.BindableService {

    /**
     */
    public void checkSeg(generated.xn.services.ex.api.grpcstream.protos.SegmentReq request,
        io.grpc.stub.StreamObserver<generated.xn.services.ex.api.grpcstream.protos.SegmentResp> responseObserver) {
      asyncUnimplementedUnaryCall(getCheckSegMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCheckSegMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                generated.xn.services.ex.api.grpcstream.protos.SegmentReq,
                generated.xn.services.ex.api.grpcstream.protos.SegmentResp>(
                  this, METHODID_CHECK_SEG)))
          .build();
    }
  }

  /**
   */
  public static final class SegmentServicesStub extends io.grpc.stub.AbstractStub<SegmentServicesStub> {
    private SegmentServicesStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SegmentServicesStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SegmentServicesStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SegmentServicesStub(channel, callOptions);
    }

    /**
     */
    public void checkSeg(generated.xn.services.ex.api.grpcstream.protos.SegmentReq request,
        io.grpc.stub.StreamObserver<generated.xn.services.ex.api.grpcstream.protos.SegmentResp> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCheckSegMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SegmentServicesBlockingStub extends io.grpc.stub.AbstractStub<SegmentServicesBlockingStub> {
    private SegmentServicesBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SegmentServicesBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SegmentServicesBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SegmentServicesBlockingStub(channel, callOptions);
    }

    /**
     */
    public generated.xn.services.ex.api.grpcstream.protos.SegmentResp checkSeg(generated.xn.services.ex.api.grpcstream.protos.SegmentReq request) {
      return blockingUnaryCall(
          getChannel(), getCheckSegMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SegmentServicesFutureStub extends io.grpc.stub.AbstractStub<SegmentServicesFutureStub> {
    private SegmentServicesFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SegmentServicesFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SegmentServicesFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SegmentServicesFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<generated.xn.services.ex.api.grpcstream.protos.SegmentResp> checkSeg(
        generated.xn.services.ex.api.grpcstream.protos.SegmentReq request) {
      return futureUnaryCall(
          getChannel().newCall(getCheckSegMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CHECK_SEG = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SegmentServicesImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SegmentServicesImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CHECK_SEG:
          serviceImpl.checkSeg((generated.xn.services.ex.api.grpcstream.protos.SegmentReq) request,
              (io.grpc.stub.StreamObserver<generated.xn.services.ex.api.grpcstream.protos.SegmentResp>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class SegmentServicesBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SegmentServicesBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return generated.xn.services.ex.api.grpcstream.protos.SegmentProtos.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SegmentServices");
    }
  }

  private static final class SegmentServicesFileDescriptorSupplier
      extends SegmentServicesBaseDescriptorSupplier {
    SegmentServicesFileDescriptorSupplier() {}
  }

  private static final class SegmentServicesMethodDescriptorSupplier
      extends SegmentServicesBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SegmentServicesMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (SegmentServicesGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SegmentServicesFileDescriptorSupplier())
              .addMethod(getCheckSegMethod())
              .build();
        }
      }
    }
    return result;
  }
}
