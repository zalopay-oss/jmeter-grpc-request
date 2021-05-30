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
    private static final Path GRPC_DUMMY_SERVER_FOLDER = Paths.get("/dist/benchmark/grpc-server/dist");
    private static final Path PROTO_WITH_EXTERNAL_IMPORT_FOLDER =
            Paths.get("dist/benchmark/grpc-server/src/main/resources/protos-v2");
    private static final Path PROTO_FOLDER =
            Paths.get("dist/benchmark/grpc-server/src/main/resources/protos");
    private static final Path LIB_FOLDER =
            Paths.get("dist/benchmark/grpc-server/src/main/resources/libs");

    private static String HOST_PORT = "localhost:8005";
    private static String REQUEST_JSON = "{\"shelf\":{\"id\":1599156420811,\"theme\":\"Hello server!!\"}}";
    private static String FULL_METHOD = "bookstore.Bookstore/CreateShelf";
    private static boolean TLS = Boolean.FALSE;
    private static boolean TLS_DISABLE_VERIFICATION = false;
    private static String METADATA = "";

    Process bookStoreServer;
    Process helloWorldServer;
    File dummyLog;

    @BeforeClass
    public void setupDependencies() throws IOException {
        File javaHome = new File(System.getProperty("java.home"), "bin");
        String javaPath = javaHome + File.separator + "java";
        String startClassPathCommand = String.format("%s", Paths.get(System.getProperty("user.dir"), GRPC_DUMMY_SERVER_FOLDER.toString(), GRPC_DUMMY_SERVER_JAR.toString()).toString());
        ProcessBuilder startBookStoreGRPCServerProcessBuilder = new ProcessBuilder(javaPath, "-cp", startClassPathCommand, "server.BookStoreServer");
        ProcessBuilder startHelloWorldGRPCServerProcessBuilder = new ProcessBuilder(javaPath, "-cp", startClassPathCommand, "server.HelloWorldServer");
        dummyLog = new File("grpc-dummy-server.log");
        startBookStoreGRPCServerProcessBuilder.redirectError(ProcessBuilder.Redirect.appendTo(dummyLog));
        startHelloWorldGRPCServerProcessBuilder.redirectError(ProcessBuilder.Redirect.appendTo(dummyLog));
        bookStoreServer = startBookStoreGRPCServerProcessBuilder.start();
        helloWorldServer = startHelloWorldGRPCServerProcessBuilder.start();
    }

    @Test
    public void testCanSendGrpcUnaryRequest() {
        ClientCaller clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, TLS, TLS_DISABLE_VERIFICATION, METADATA);
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
        helloWorldServer.destroy();
        dummyLog.delete();
    }

}
