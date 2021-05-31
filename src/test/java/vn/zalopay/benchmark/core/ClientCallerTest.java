package vn.zalopay.benchmark.core;

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
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("1000");
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
        dummyLog.delete();
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
        bookStoreServer = startBookStoreGRPCServerProcessBuilder.start();
        helloWorldServer = startHelloWorldGRPCServerProcessBuilder.start();
        bookStoreTlsServer = startBookStoreTlsGRPCServerProcessBuilder.start();
        helloWorldTlsServer = startHelloWorldTlsGRPCServerProcessBuilder.start();
    }
}
