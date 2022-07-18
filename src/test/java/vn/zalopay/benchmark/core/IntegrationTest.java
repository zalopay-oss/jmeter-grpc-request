package vn.zalopay.benchmark.core;

import com.google.common.net.HostAndPort;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.reporters.AbstractListenerElement;
import org.apache.jmeter.samplers.*;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import vn.zalopay.benchmark.GRPCSampler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IntegrationTest extends BaseTest {
    private static volatile List<SampleResult> SAMPLE_RESULTS = new ArrayList<>();

    @Test(timeOut = 60000)
    public void canRunJMeterScript() throws IOException {
        // Initialize properties
        JMeterUtils.helpGC();
        JMeterUtils.setJMeterHome(TEMP_JMETER_HOME.toString());
        JMeterUtils.initLogging();
        JMeterUtils.loadJMeterProperties(JMETER_PROPERTIES_FILE.toString());
        JMeterUtils.initLocale();
        JMeterUtils.setProperty("jmeterengine.force.system.exit", "false");
        JMeterUtils.setProperty("DEBUG", "true");
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        // JMeter Test Plan, basically JOrphan HashTree
        HashTree testPlanTree = new HashTree();
        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(2);
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

        SaveService.saveTree(
                testPlanTree,
                new FileOutputStream(
                        Paths.get(TEMP_JMETER_HOME.toString(), "IntegrationTest.jmx").toString()));
        // add Summarizer output to get test progress in stdout like:
        // Store execution results into a .jtl file
        IntegrationTestResultCollector testResult = new IntegrationTestResultCollector();
        testPlanTree.add(testPlanTree.getArray()[0], new Object[] {testResult});

        // Run Test Plan
        jmeter.configure(testPlanTree);
        jmeter.run();
        jmeter.askThreadsToStop();
        jmeter.stopTest();
        // Assert
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(SAMPLE_RESULTS.size(), 100);
        SAMPLE_RESULTS.forEach(s -> softAssert.assertEquals(s.getResponseCode(), "200"));
        SAMPLE_RESULTS.forEach(
                s ->
                        softAssert.assertTrue(
                                new String(s.getResponseData())
                                        .contains("\"theme\": \"Hello server")));
        softAssert.assertAll();
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

    class IntegrationTestResultCollector extends AbstractListenerElement
            implements SampleListener,
                    Clearable,
                    Serializable,
                    TestStateListener,
                    Remoteable,
                    NoThreadClone {

        public IntegrationTestResultCollector() {}

        @Override
        public synchronized void sampleOccurred(SampleEvent sampleEvent) {
            SampleResult result = sampleEvent.getResult();
            SAMPLE_RESULTS.add(result);
        }

        @Override
        public void sampleStarted(SampleEvent sampleEvent) {}

        @Override
        public void sampleStopped(SampleEvent sampleEvent) {}

        @Override
        public void clearData() {}

        @Override
        public void testStarted() {}

        @Override
        public void testStarted(String s) {}

        @Override
        public void testEnded() {}

        @Override
        public void testEnded(String s) {}
    }
}
