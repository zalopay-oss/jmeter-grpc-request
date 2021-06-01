package vn.zalopay.benchmark.core;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.netty.GrpcSslContexts;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.SslContextBuilder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.core.specification.GrpcResponse;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.any;


public class ClientCallerTest {
    private static final String GRPC_DUMMY_SERVER_JAR = "gprc-server-1.0-SNAPSHOT.jar";
    private static final Path GRPC_DUMMY_SERVER_FOLDER = Paths.get(System.getProperty("user.dir"), "/dist/benchmark/grpc-server/dist");
    private static final Path PROTO_WITH_EXTERNAL_IMPORT_FOLDER =
            Paths.get(System.getProperty("user.dir"), "dist/benchmark/grpc-server/src/main/resources/protos-v2");
    private static final Path PROTO_FOLDER =
            Paths.get(System.getProperty("user.dir"), "dist/benchmark/grpc-server/src/main/resources/protos");
    private static final Path LIB_FOLDER =
            Paths.get(System.getProperty("user.dir"), "dist/benchmark/grpc-server/src/main/resources/libs");

    private static String HOST_PORT = "localhost:8005";
    private static String HOST_PORT_TLS = "localhost:8006";
    private static String REQUEST_JSON = "{\"shelf\":{\"id\":1599156420811,\"theme\":\"Hello server!!\"}}";
    private static String FULL_METHOD = "bookstore.Bookstore/CreateShelf";
    private static String METADATA = "";

    static Process bookStoreServer;
    static Process helloWorldServer;
    static Process bookStoreTlsServer;
    static Process helloWorldTlsServer;
    static File dummyLog;

    @BeforeClass
    public void setupDependencies() throws IOException {
        System.setProperty("javax.net.ssl.trustStore", Paths.get(System.getProperty("user.dir"), "dist", "cert", "cacert").toString());
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        startDummyGrpcServer();
    }

    @Test
    public void testCanSendGrpcUnaryRequest() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1:1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("2000");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanCallClientStreamingRequest() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1:1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callClientStreaming("2000");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanCallServerStreamingRequest() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1:1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callServerStreaming("2000");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanCallBidiStreamingRequest() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1:1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callBidiStreaming("2000");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendRequestWithNegativeTimeoutRequest() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("-10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Metadata entry must be defined in key1:value1,key2:value2 format: key1=1,key2:2")
    public void testCanThrowExceptionWithInvalidMetaData() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1=1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("2000");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendGrpcUnaryRequestWithMetaData() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("2000");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendGrpcUnaryRequestWithSSLAndDisableSSLVerification() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT_TLS, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, true, true, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10000");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendGrpcUnaryRequestWithSSLAndEnableSSLVerification() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT_TLS, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, true, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10000");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing deadline to long")
    public void testThrowExceptionWithInvalidTimeoutFormat() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        clientCaller.call("1000s");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing deadline to long")
    public void testThrowExceptionWithBlankTimeoutFormat() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        clientCaller.call(" ");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing deadline to long")
    public void testThrowExceptionWithEmptyTimeoutFormat() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        clientCaller.call("");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing deadline to long")
    public void testThrowExceptionWithNullTimeoutFormat() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        clientCaller.call(null);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing request for rpc")
    public void testThrowExceptionWithInvalidRequestJson() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest("{shelf:{\"id\":1599156420811,\"theme\":\"Hello server!!\".}}");
        clientCaller.call("1000");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing request for rpc")
    public void testThrowExceptionWithParsingRequestToJson() {
        MockedStatic<com.google.protobuf.util.JsonFormat> jsonFormat = Mockito.mockStatic(com.google.protobuf.util.JsonFormat.class);
        jsonFormat.when(JsonFormat::printer).then((i) -> new InvalidProtocolBufferException("Dummy Exception"));
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest("{shelf:{\"id\":1599156420811,\"theme\":\"Hello server!!\".}}");
        clientCaller.call("1000");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unable to resolve service by invoking protoc")
    public void testThrowExceptionWithExceptionInProtocInvoke() {
        MockedStatic<ProtocInvoker> protocInvoker = Mockito.mockStatic(ProtocInvoker.class);
        protocInvoker.when(() -> ProtocInvoker.forConfig(Mockito.anyString(), Mockito.anyString()).invoke()).thenThrow(new RuntimeException("Dummy Exception"));
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest("{shelf:{\"id\":1599156420811,\"theme\":\"Hello server!!\"}}");
        clientCaller.call("1000s");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Error in create SSL connection!")
    public void testCanThrowExceptionWithSSLException() {
        MockedStatic<io.grpc.netty.GrpcSslContexts> grpcSslContextBuilder = Mockito.mockStatic(io.grpc.netty.GrpcSslContexts.class);
        grpcSslContextBuilder.when(() -> GrpcSslContexts.forClient()
        ).then(invocation -> {
                    SslContextBuilder sslContext = Mockito.mock(SslContextBuilder.class);
                    Mockito.when(sslContext.applicationProtocolConfig(any(ApplicationProtocolConfig.class))).then(i -> sslContext);
                    Mockito.when(sslContext.build()).thenThrow(new SSLException("Dummy Exception"));
                    return sslContext;
                }
        );
        new ClientCaller("localhost:1231", PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, true, false, METADATA);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Error in create SSL connection!")
    public void testCanThrowExceptionWithSSLExceptionAndDisableSSLVerification() {
        MockedStatic<io.grpc.netty.GrpcSslContexts> grpcSslContextBuilder = Mockito.mockStatic(io.grpc.netty.GrpcSslContexts.class);
        grpcSslContextBuilder.when(() -> GrpcSslContexts.forClient()
        ).then(invocation -> {
                    SslContextBuilder sslContext = Mockito.mock(SslContextBuilder.class);
                    Mockito.when(sslContext.applicationProtocolConfig(any(ApplicationProtocolConfig.class))).then(i -> sslContext);
                    Mockito.when(sslContext.build()).thenThrow(new SSLException("Dummy Exception"));
                    return sslContext;
                }
        );
        new ClientCaller("localhost:1231", PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, true, true, METADATA);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while waiting for rpc")
    public void testThrowExceptionWithTimeoutRequest() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while waiting for rpc")
    public void testThrowExceptionWithTimeoutRequestServerStream() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callServerStreaming("10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while waiting for rpc")
    public void testThrowExceptionWithTimeoutRequestClientStream() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callClientStreaming("10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while waiting for rpc")
    public void testThrowExceptionWithTimeoutRequestBidiStream() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callBidiStreaming("10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unable to find method invalidName in service Bookstore")
    public void testThrowExceptionWithInvalidMethodName() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstore.Bookstore/invalidName", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "fullMethodName")
    public void testThrowExceptionWithNullMethodName() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), null, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Could not extract full service from  ")
    public void testThrowExceptionWithBlankMethodName() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), " ", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Could not extract full service from ")
    public void testThrowExceptionWithEmptyMethodName() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Could not extract package name from bookstoreBookstore")
    public void testThrowExceptionWithPackageAndServiceMethodName() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstoreBookstore/CreateShelf", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Could not extract service from bookstoreBookstore.")
    public void testThrowExceptionWithInvalidPackagedName() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstoreBookstore./CreateShelf", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Could not extract method name from bookstore.Bookstore/")
    public void testThrowExceptionWithInvalidMethodNameWithDoubleSlash() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstore.Bookstore/", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Unable to find service with name: Bookstores")
    public void testThrowExceptionWithInvalidServiceName() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstore.Bookstores/CreateShelf", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("1000");
        clientCaller.shutdown();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }


    @AfterMethod
    public void cleanMockito() {
        Mockito.clearAllCaches();
    }

    @AfterClass
    public void shutdownDummyServer() {
        bookStoreServer.destroy();
        bookStoreServer.destroyForcibly();
        helloWorldServer.destroy();
        helloWorldServer.destroyForcibly();
        bookStoreTlsServer.destroy();
        bookStoreTlsServer.destroyForcibly();
        helloWorldTlsServer.destroy();
        helloWorldTlsServer.destroyForcibly();
    }

    private void startDummyGrpcServer() throws IOException {
        File javaHome = new File(System.getProperty("java.home"), "bin");
        String javaPath = javaHome + File.separator + "java";
        String startClassPathCommand = String.format("%s", Paths.get(GRPC_DUMMY_SERVER_FOLDER.toString(), GRPC_DUMMY_SERVER_JAR.toString()).toString());
        ProcessBuilder startBookStoreGRPCServerProcessBuilder = new ProcessBuilder(javaPath, "-cp", startClassPathCommand, "server.BookStoreServer");
        ProcessBuilder startHelloWorldGRPCServerProcessBuilder = new ProcessBuilder(javaPath, "-cp", startClassPathCommand, "server.HelloWorldServer");
        ProcessBuilder startBookStoreTlsGRPCServerProcessBuilder = new ProcessBuilder(javaPath, "-cp", startClassPathCommand, "server.BookStoreServerTls");
        ProcessBuilder startHelloWorldTlsGRPCServerProcessBuilder = new ProcessBuilder(javaPath, "-cp", startClassPathCommand, "server.HelloWorldServerTls");
        dummyLog = new File("grpc-dummy-server.log");
        startBookStoreGRPCServerProcessBuilder.redirectError(ProcessBuilder.Redirect.appendTo(dummyLog));
        startHelloWorldGRPCServerProcessBuilder.redirectError(ProcessBuilder.Redirect.appendTo(dummyLog));
        startBookStoreTlsGRPCServerProcessBuilder.redirectError(ProcessBuilder.Redirect.appendTo(dummyLog));
        startHelloWorldTlsGRPCServerProcessBuilder.redirectError(ProcessBuilder.Redirect.appendTo(dummyLog));
        bookStoreServer = startBookStoreGRPCServerProcessBuilder.start();
        helloWorldServer = startHelloWorldGRPCServerProcessBuilder.start();
        bookStoreTlsServer = startBookStoreTlsGRPCServerProcessBuilder.start();
        helloWorldTlsServer = startHelloWorldTlsGRPCServerProcessBuilder.start();
    }
}
