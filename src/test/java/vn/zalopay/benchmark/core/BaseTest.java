package vn.zalopay.benchmark.core;

import kg.apc.emulators.TestJMeterUtils;

import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import vn.zalopay.benchmark.core.config.GrpcRequestConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTest {
    private static final String GRPC_DUMMY_SERVER_JAR = "gprc-server-1.0-SNAPSHOT.jar";
    private static final Path GRPC_DUMMY_SERVER_FOLDER =
            Paths.get(System.getProperty("user.dir"), "/dist/benchmark/grpc-server/dist");
    protected static int DEFAULT_CHANNEL_SHUTDOWN_TIME = 5000;
    protected static final Path TEMP_JMETER_HOME =
            Paths.get(System.getProperty("user.dir"), "src", "test", "resources");
    protected static final Path JMETER_PROPERTIES_FILE =
            Paths.get(
                    System.getProperty("user.dir"),
                    "src",
                    "test",
                    "resources",
                    "jmeter.properties");
    protected static final Path PROTO_WITH_EXTERNAL_IMPORT_FOLDER =
            Paths.get(
                    System.getProperty("user.dir"),
                    "dist/benchmark/grpc-server/src/main/resources/protos-v2");
    protected static final Path PROTO_PATH_WITH_INVALID_FILE_PATH =
            Paths.get(
                    System.getProperty("user.dir"),
                    "dist/benchmark/grpc-server/src/main/resources/protos-v2/shelf.proto1");
    protected static final Path PROTO_FOLDER =
            Paths.get(
                    System.getProperty("user.dir"),
                    "dist/benchmark/grpc-server/src/main/resources/protos");
    protected static final Path LIB_FOLDER =
            Paths.get(
                    System.getProperty("user.dir"),
                    "dist/benchmark/grpc-server/src/main/resources/libs");
    protected static String HOST_PORT = "localhost:8005";
    protected static String HOST_PORT_TLS = "localhost:8006";
    protected static String REQUEST_JSON =
            "{\"shelf\":{\"id\":1599156420811,\"theme\":\"Hello server!!\"}}";
    protected static String FULL_METHOD = "bookstore.Bookstore/CreateShelf";
    protected static String METADATA = "";
    protected static final GrpcRequestConfig DEFAULT_GRPC_REQUEST_CONFIG =
            new GrpcRequestConfig(
                    HOST_PORT,
                    PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(),
                    LIB_FOLDER.toString(),
                    FULL_METHOD,
                    false,
                    false,
                    DEFAULT_CHANNEL_SHUTDOWN_TIME);
    // JSON METADATA TEST
    protected static String METADATA_JSON = "{\"key1\":\"Value1\"}";
    protected static String FULL_METHOD_WITH_METADATA =
            "helloworld.Greeter/SayHelloWithJsonMetadata";
    protected static String METADATA_REQUEST_JSON = "{\"name\": \"User\"}";
    protected static String EXPECTED_RESPONSE_DATA =
            "{\n  \"message\": \"Hello User : Metadata : Value1\"\n}";
    protected ClientCaller clientCaller;
    private static Process bookStoreServer;
    private static Process helloWorldServer;
    private static Process bookStoreTlsServer;
    private static Process helloWorldTlsServer;
    private static File dummyLog;

    @BeforeSuite
    public void setupDependencies() throws IOException {
        System.setProperty(
                "javax.net.ssl.trustStore",
                Paths.get(System.getProperty("user.dir"), "dist", "cert", "cacert").toString());
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        startDummyGrpcServer();
        TestJMeterUtils.createJmeterEnv();
    }

    @BeforeMethod
    public void cleanMockitoBeforeMethod() {
        Mockito.clearAllCaches();
    }

    @AfterMethod
    public void cleanMockitoAfterMethod() {
        Mockito.clearAllCaches();
        if (clientCaller != null) clientCaller.shutdownNettyChannel();
        clientCaller = null;
    }

    @AfterSuite
    public void shutdownDummyServer() throws InterruptedException {
        bookStoreServer.destroy();
        if (bookStoreServer.isAlive()) bookStoreServer.destroyForcibly();
        helloWorldServer.destroy();
        if (helloWorldServer.isAlive()) helloWorldServer.destroyForcibly();
        bookStoreTlsServer.destroy();
        if (bookStoreTlsServer.isAlive()) bookStoreTlsServer.destroyForcibly();
        helloWorldTlsServer.destroy();
        if (helloWorldTlsServer.isAlive()) helloWorldTlsServer.destroyForcibly();
    }

    private void startDummyGrpcServer() throws IOException {
        File javaHome = new File(System.getProperty("java.home"), "bin");
        String javaPath = javaHome + File.separator + "java";
        String startClassPathCommand =
                String.format(
                        "%s",
                        Paths.get(
                                        GRPC_DUMMY_SERVER_FOLDER.toString(),
                                        GRPC_DUMMY_SERVER_JAR.toString())
                                .toString());
        ProcessBuilder startBookStoreGRPCServerProcessBuilder =
                new ProcessBuilder(
                        new String[] {
                            javaPath, "-cp", startClassPathCommand, "server.BookStoreServer"
                        });
        ProcessBuilder startHelloWorldGRPCServerProcessBuilder =
                new ProcessBuilder(
                        new String[] {
                            javaPath, "-cp", startClassPathCommand, "server.HelloWorldServer"
                        });
        ProcessBuilder startBookStoreTlsGRPCServerProcessBuilder =
                new ProcessBuilder(
                        new String[] {
                            javaPath, "-cp", startClassPathCommand, "server.BookStoreServerTls"
                        });
        ProcessBuilder startHelloWorldTlsGRPCServerProcessBuilder =
                new ProcessBuilder(
                        new String[] {
                            javaPath, "-cp", startClassPathCommand, "server.HelloWorldServerTls"
                        });
        dummyLog = new File("grpc-dummy-server.log");
        startBookStoreGRPCServerProcessBuilder.redirectError(
                ProcessBuilder.Redirect.appendTo(dummyLog));
        startHelloWorldGRPCServerProcessBuilder.redirectError(
                ProcessBuilder.Redirect.appendTo(dummyLog));
        startBookStoreTlsGRPCServerProcessBuilder.redirectError(
                ProcessBuilder.Redirect.appendTo(dummyLog));
        startHelloWorldTlsGRPCServerProcessBuilder.redirectError(
                ProcessBuilder.Redirect.appendTo(dummyLog));
        bookStoreServer = startBookStoreGRPCServerProcessBuilder.start();
        helloWorldServer = startHelloWorldGRPCServerProcessBuilder.start();
        bookStoreTlsServer = startBookStoreTlsGRPCServerProcessBuilder.start();
        helloWorldTlsServer = startHelloWorldTlsGRPCServerProcessBuilder.start();
    }
}
