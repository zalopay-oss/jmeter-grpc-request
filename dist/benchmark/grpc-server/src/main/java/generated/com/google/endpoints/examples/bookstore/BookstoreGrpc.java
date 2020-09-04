package generated.com.google.endpoints.examples.bookstore;

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
 * <pre>
 * A simple Bookstore API.
 * The API manages shelves and books resources. Shelves contain books.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.18.0)",
    comments = "Source: http_bookstore.proto")
public final class BookstoreGrpc {

  private BookstoreGrpc() {}

  public static final String SERVICE_NAME = "bookstore.Bookstore";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      generated.com.google.endpoints.examples.bookstore.ListShelvesResponse> getListShelvesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListShelves",
      requestType = com.google.protobuf.Empty.class,
      responseType = generated.com.google.endpoints.examples.bookstore.ListShelvesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      generated.com.google.endpoints.examples.bookstore.ListShelvesResponse> getListShelvesMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, generated.com.google.endpoints.examples.bookstore.ListShelvesResponse> getListShelvesMethod;
    if ((getListShelvesMethod = BookstoreGrpc.getListShelvesMethod) == null) {
      synchronized (BookstoreGrpc.class) {
        if ((getListShelvesMethod = BookstoreGrpc.getListShelvesMethod) == null) {
          BookstoreGrpc.getListShelvesMethod = getListShelvesMethod = 
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, generated.com.google.endpoints.examples.bookstore.ListShelvesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "bookstore.Bookstore", "ListShelves"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.com.google.endpoints.examples.bookstore.ListShelvesResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new BookstoreMethodDescriptorSupplier("ListShelves"))
                  .build();
          }
        }
     }
     return getListShelvesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<generated.com.google.endpoints.examples.bookstore.CreateShelfRequest,
      generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf> getCreateShelfMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateShelf",
      requestType = generated.com.google.endpoints.examples.bookstore.CreateShelfRequest.class,
      responseType = generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<generated.com.google.endpoints.examples.bookstore.CreateShelfRequest,
      generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf> getCreateShelfMethod() {
    io.grpc.MethodDescriptor<generated.com.google.endpoints.examples.bookstore.CreateShelfRequest, generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf> getCreateShelfMethod;
    if ((getCreateShelfMethod = BookstoreGrpc.getCreateShelfMethod) == null) {
      synchronized (BookstoreGrpc.class) {
        if ((getCreateShelfMethod = BookstoreGrpc.getCreateShelfMethod) == null) {
          BookstoreGrpc.getCreateShelfMethod = getCreateShelfMethod = 
              io.grpc.MethodDescriptor.<generated.com.google.endpoints.examples.bookstore.CreateShelfRequest, generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "bookstore.Bookstore", "CreateShelf"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.com.google.endpoints.examples.bookstore.CreateShelfRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf.getDefaultInstance()))
                  .setSchemaDescriptor(new BookstoreMethodDescriptorSupplier("CreateShelf"))
                  .build();
          }
        }
     }
     return getCreateShelfMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BookstoreStub newStub(io.grpc.Channel channel) {
    return new BookstoreStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BookstoreBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new BookstoreBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static BookstoreFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new BookstoreFutureStub(channel);
  }

  /**
   * <pre>
   * A simple Bookstore API.
   * The API manages shelves and books resources. Shelves contain books.
   * </pre>
   */
  public static abstract class BookstoreImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Returns a list of all shelves in the bookstore.
     * </pre>
     */
    public void listShelves(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<generated.com.google.endpoints.examples.bookstore.ListShelvesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListShelvesMethod(), responseObserver);
    }

    /**
     * <pre>
     * Creates a new shelf in the bookstore.
     * </pre>
     */
    public void createShelf(generated.com.google.endpoints.examples.bookstore.CreateShelfRequest request,
        io.grpc.stub.StreamObserver<generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateShelfMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getListShelvesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                generated.com.google.endpoints.examples.bookstore.ListShelvesResponse>(
                  this, METHODID_LIST_SHELVES)))
          .addMethod(
            getCreateShelfMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                generated.com.google.endpoints.examples.bookstore.CreateShelfRequest,
                generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf>(
                  this, METHODID_CREATE_SHELF)))
          .build();
    }
  }

  /**
   * <pre>
   * A simple Bookstore API.
   * The API manages shelves and books resources. Shelves contain books.
   * </pre>
   */
  public static final class BookstoreStub extends io.grpc.stub.AbstractStub<BookstoreStub> {
    private BookstoreStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BookstoreStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BookstoreStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new BookstoreStub(channel, callOptions);
    }

    /**
     * <pre>
     * Returns a list of all shelves in the bookstore.
     * </pre>
     */
    public void listShelves(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<generated.com.google.endpoints.examples.bookstore.ListShelvesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListShelvesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Creates a new shelf in the bookstore.
     * </pre>
     */
    public void createShelf(generated.com.google.endpoints.examples.bookstore.CreateShelfRequest request,
        io.grpc.stub.StreamObserver<generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateShelfMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * A simple Bookstore API.
   * The API manages shelves and books resources. Shelves contain books.
   * </pre>
   */
  public static final class BookstoreBlockingStub extends io.grpc.stub.AbstractStub<BookstoreBlockingStub> {
    private BookstoreBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BookstoreBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BookstoreBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new BookstoreBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Returns a list of all shelves in the bookstore.
     * </pre>
     */
    public generated.com.google.endpoints.examples.bookstore.ListShelvesResponse listShelves(com.google.protobuf.Empty request) {
      return blockingUnaryCall(
          getChannel(), getListShelvesMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Creates a new shelf in the bookstore.
     * </pre>
     */
    public generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf createShelf(generated.com.google.endpoints.examples.bookstore.CreateShelfRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateShelfMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * A simple Bookstore API.
   * The API manages shelves and books resources. Shelves contain books.
   * </pre>
   */
  public static final class BookstoreFutureStub extends io.grpc.stub.AbstractStub<BookstoreFutureStub> {
    private BookstoreFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BookstoreFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BookstoreFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new BookstoreFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Returns a list of all shelves in the bookstore.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<generated.com.google.endpoints.examples.bookstore.ListShelvesResponse> listShelves(
        com.google.protobuf.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(getListShelvesMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Creates a new shelf in the bookstore.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf> createShelf(
        generated.com.google.endpoints.examples.bookstore.CreateShelfRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateShelfMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_LIST_SHELVES = 0;
  private static final int METHODID_CREATE_SHELF = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final BookstoreImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(BookstoreImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_LIST_SHELVES:
          serviceImpl.listShelves((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<generated.com.google.endpoints.examples.bookstore.ListShelvesResponse>) responseObserver);
          break;
        case METHODID_CREATE_SHELF:
          serviceImpl.createShelf((generated.com.google.endpoints.examples.bookstore.CreateShelfRequest) request,
              (io.grpc.stub.StreamObserver<generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf>) responseObserver);
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

  private static abstract class BookstoreBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BookstoreBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return generated.com.google.endpoints.examples.bookstore.BookstoreProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Bookstore");
    }
  }

  private static final class BookstoreFileDescriptorSupplier
      extends BookstoreBaseDescriptorSupplier {
    BookstoreFileDescriptorSupplier() {}
  }

  private static final class BookstoreMethodDescriptorSupplier
      extends BookstoreBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    BookstoreMethodDescriptorSupplier(String methodName) {
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
      synchronized (BookstoreGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new BookstoreFileDescriptorSupplier())
              .addMethod(getListShelvesMethod())
              .addMethod(getCreateShelfMethod())
              .build();
        }
      }
    }
    return result;
  }
}
