package vn.zalopay.benchmark.core;

import com.google.common.net.HostAndPort;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.testng.annotations.Test;
import vn.zalopay.benchmark.GRPCSampler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IntegrationTest extends BaseTest {
    @Test
    public void canRunJMeterScript() throws IOException {
        // Initialize properties
        Path tempJmeterHome = Paths.get(System.getProperty("user.dir"), "src", "test", "resources");
        Path jmeterPropertiesFile = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "jmeter.properties");
        JMeterUtils.setJMeterHome(tempJmeterHome.toString());
        JMeterUtils.loadJMeterProperties(jmeterPropertiesFile.toString());
        JMeterUtils.initLocale();
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        // JMeter Test Plan, basically JOrphan HashTree
        HashTree testPlanTree = new HashTree();
        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(5);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();
        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Example Thread Group");
        threadGroup.setNumThreads(50);
        threadGroup.setRampUp(0);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        // Test Plan
        TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        // Construct Test Plan from previously initialized elements
        testPlanTree.add(testPlan);
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(createGrpcSampler());

        SaveService.saveTree(testPlanTree, new FileOutputStream(Paths.get(tempJmeterHome.toString(), "IntegrationTest.jmx").toString()));
        //add Summarizer output to get test progress in stdout like:
        // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }
        // Store execution results into a .jtl file
        String logFile = "jmeter-integration-test.jtl";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(logFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run Test Plan
        jmeter.configure(testPlanTree);
        jmeter.run();
    }


    private GRPCSampler createGrpcSampler() {
        HostAndPort hostAndPort = HostAndPort.fromString(HOST_PORT);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString());
        grpcSampler.setLibFolder(LIB_FOLDER.toString());
        grpcSampler.setMetadata(METADATA);
        grpcSampler.setHost(hostAndPort.getHost());
        grpcSampler.setPort(Integer.toString(hostAndPort.getPort()));
        grpcSampler.setFullMethod(FULL_METHOD);
        grpcSampler.setDeadline("2000");
        grpcSampler.setTls(false);
        grpcSampler.setTlsDisableVerification(false);
        grpcSampler.setRequestJson(REQUEST_JSON);
        return grpcSampler;
    }
}
