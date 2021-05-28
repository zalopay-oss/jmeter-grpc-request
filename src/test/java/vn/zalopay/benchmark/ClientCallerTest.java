package vn.zalopay.benchmark;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.zalopay.benchmark.core.ClientCaller;
import vn.zalopay.benchmark.core.specification.GrpcResponse;

import java.nio.file.Path;
import java.nio.file.Paths;

@Ignore
public class ClientCallerTest {
    private static final Logger logger = LoggerFactory.getLogger(ClientCallerTest.class);

    private static final Path PROTO_FOLDER = Paths.get("dist/benchmark/grpc-server/src/main/resources/protos-v2");

    /* Download at https://github.com/googleapis/googleapis */
    private static final Path LIB_FOLDER = Paths.get("/Users/lap13227/Desktop/request-proto/googleapis-master");
    private static String HOST_PORT = "localhost:8005";
    private static String REQUEST_JSON = "{\"shelf\":{\"id\":1599156420811,\"theme\":\"Hello server!!\"}}";
    private static String FULL_METHOD = "bookstore.Bookstore/CreateShelf";
    private static boolean TLS = Boolean.FALSE;
    private static boolean TLS_DISABLE_VERIFICATION = false;
    private static String METADATA = "";

    private ClientCaller clientCaller;

    @Before
    public void setup() {
        logger.info("Setup test");
        clientCaller = new ClientCaller(HOST_PORT, PROTO_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, TLS, TLS_DISABLE_VERIFICATION, METADATA);
    }

    @Test
    public void test() {
        logger.info("Main test");
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10000");
        clientCaller.shutdown();
        logger.info(resp.getGrpcMessageString());
        System.out.println(resp.getGrpcMessageString());

    }

}
