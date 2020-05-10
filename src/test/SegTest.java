//import org.apache.jmeter.config.Arguments;
//import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
//import org.apache.jmeter.samplers.SampleResult;
//import org.junit.Test;
//
//import java.util.logging.Logger;
//
//public class SegTest {
//    private static Logger log = Logger.getLogger(String.valueOf(SegTest.class));
//
//
//    String TEST_PROTO_FILES = "/Users/lap13227/workspace/work/performance-team/research/repos/jlot/jlotm-master/src/test/java/resources/protos";
//    String HOST_PORT = "localhost:50054";
//    String REQUEST_FILE = "/Users/lap13227/workspace/work/performance-team/research/repos/jlot/jlotm-master/src/test/java/resources/requests/request-seguser.json";
//    String FULL_METHOD = "data_services_seg.SegmentServices/checkSeg";
//    String deadline = "10000";
//
//    @Test
//    public void time() {
//        JplotmSimpler jplotmSimpler = new JplotmSimpler();
//        Arguments defaultParameters = new Arguments();
//        defaultParameters.addArgument("TEST_PROTO_FILES", TEST_PROTO_FILES);
//        defaultParameters.addArgument("HOST_PORT", HOST_PORT);
//        defaultParameters.addArgument("REQUEST_FILE", REQUEST_FILE);
//        defaultParameters.addArgument("FULL_METHOD", FULL_METHOD);
//        defaultParameters.addArgument("deadline", deadline);
//        JavaSamplerContext context = new JavaSamplerContext(defaultParameters);
//
//        jplotmSimpler.setupTest(context);
//        SampleResult sampleResult = jplotmSimpler.runTest(context);
//        log.info("SampleResult:");
//        log.info(sampleResult.getResponseDataAsString());
//        jplotmSimpler.teardownTest(context);
//    }
//
//}
