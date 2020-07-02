package vn.zalopay.benchmark;

import com.google.protobuf.DynamicMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.zalopay.benchmark.core.ClientCaller;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientCallerTest {
    private static final Logger logger = LoggerFactory.getLogger(ClientCallerTest.class);

//    private static final Path PROTO_FOLDER = Paths.get(getWorkspaceRoot().toString(),
//            "dist", "benchmark", "grpc-server", "src", "main", "resources", "protos");
//    private static final Path LIB_FOLDER = Paths.get(getWorkspaceRoot().toString(),
//        "dist", "benchmark", "grpc-server", "src", "main", "resources", "protos");
//    private static String HOST_PORT = "localhost:8005";
//    private static Path REQUEST_FILE = Paths.get(getWorkspaceRoot().toString(),
//            "dist", "benchmark", "grpc-server", "src", "main", "resources", "requests", "request-seguser.json");
//    private static String REQUEST_JSON = "";
//    private static String FULL_METHOD = "data_services_seg.SegmentServices/checkSeg";
//    private static boolean TLS = Boolean.FALSE;

    private static final Path PROTO_FOLDER = Paths.get("/Users/lap13227/Desktop/request-proto/apis");
    private static final Path LIB_FOLDER = Paths.get("/Users/lap13227/Desktop/request-proto/googleapis-master");
    private static String HOST_PORT = "10.10.10.10:8110";
    private static Path REQUEST_FILE = Paths.get("/Users/lap13227/Desktop/request-proto/request.json");
    private static String REQUEST_JSON = "";
    private static String FULL_METHOD = "protobuf.AccountService/accountQuery";
    private static boolean TLS = Boolean.FALSE;
    private static String METADATA = "Authorization:Bearer USER_TOKEN";


    private ClientCaller clientCaller;

    @Before
    public void setup() {
        logger.info("Setup test");
        clientCaller = new ClientCaller(HOST_PORT, PROTO_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, TLS, METADATA);
    }

    @Ignore
    @Test
    public void test() {
        logger.info("Main test");
        clientCaller.buildRequest(REQUEST_FILE.toString(), REQUEST_JSON);
        DynamicMessage resp = clientCaller.call(10000);

        logger.info(String.format("At ClientCaller response data= %s", resp));
    }

    private static Path getWorkspaceRoot() {
        return Paths.get(".").toAbsolutePath();
    }

}
