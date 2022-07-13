package vn.zalopay.benchmark.core.sampler;

import com.google.common.net.HostAndPort;
import org.apache.jmeter.sampler.DebugSampler;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.RenderAsText;
import org.testng.Assert;
import org.testng.annotations.Test;
import vn.zalopay.benchmark.GRPCSampler;
import vn.zalopay.benchmark.GRPCSamplerGui;
import vn.zalopay.benchmark.constant.GrpcSamplerConstant;
import vn.zalopay.benchmark.core.BaseTest;
import vn.zalopay.benchmark.core.test.dependency.ViewResultsFullVisualizerGui;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

public class GRPCSamplerGuiTest extends BaseTest {

    @Test
    public void testCanShowGui() {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();
        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        Assert.assertNotNull(grpRequestPluginGUI);
        frame.dispose();
    }

    @Test
    public void testCanCreateGrpcTestElement() {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();
        GRPCSampler grpcSampler = (GRPCSampler) grpRequestPluginGUI.createTestElement();
        Assert.assertNotNull(grpcSampler);
        Assert.assertEquals(grpcSampler.getName(), "GRPC Request");
    }

    @Test
    public void testCanGetTestElementLabelResource() {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();
        Assert.assertEquals(grpRequestPluginGUI.getStaticLabel(), "GRPC Request");
        Assert.assertEquals(grpRequestPluginGUI.getLabelResource(), "grpc_sampler_title");
    }

    @Test
    public void testCanConfigureGrpcTestElement() throws InterruptedException {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();
        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        GRPCSampler grpcSampler = (GRPCSampler) grpRequestPluginGUI.createTestElement();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder("dummyProtoFolder");
        grpcSampler.setLibFolder("dummyLibFolder");
        grpcSampler.setMetadata("dummyMetadata");
        grpcSampler.setHost("dummyHost");
        grpcSampler.setPort("dummyPort");
        grpcSampler.setFullMethod("dummyMethod");
        grpcSampler.setDeadline("dummyDeadline");
        grpcSampler.setTls(true);
        grpcSampler.setTlsDisableVerification(true);
        grpcSampler.setRequestJson("dummyRequest");
        grpRequestPluginGUI.configure(grpcSampler);
        Assert.assertNotNull(grpcSampler);
        Assert.assertNotNull(grpRequestPluginGUI);
        frame.dispose();
    }


    @Test
    public void testCanClearGrpcTestElementUI() throws InterruptedException {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();
        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        GRPCSampler grpcSampler = (GRPCSampler) grpRequestPluginGUI.createTestElement();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder("dummyProtoFolder");
        grpcSampler.setLibFolder("dummyLibFolder");
        grpcSampler.setMetadata("dummyMetadata");
        grpcSampler.setHost("dummyHost");
        grpcSampler.setPort("dummyPort");
        grpcSampler.setFullMethod("dummyMethod");
        grpcSampler.setDeadline("dummyDeadline");
        grpcSampler.setTls(true);
        grpcSampler.setTlsDisableVerification(true);
        grpcSampler.setRequestJson("dummyRequest");
        grpRequestPluginGUI.configure(grpcSampler);
        grpRequestPluginGUI.clearGui();
        Assert.assertNotNull(grpcSampler);
        Assert.assertNotNull(grpRequestPluginGUI);
        frame.dispose();
    }

    @Test
    public void verifyCanConfigureWithNonGrpcSampler() {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();
        DebugSampler debugSampler = new DebugSampler();
        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        grpRequestPluginGUI.configure(debugSampler);
        Assert.assertNotNull(grpRequestPluginGUI);
        frame.dispose();
    }

    @Test
    public void verifyCanModifyWithNonGrpcSampler() {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();
        DebugSampler debugSampler = new DebugSampler();
        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        grpRequestPluginGUI.modifyTestElement(debugSampler);
        Assert.assertNotNull(grpRequestPluginGUI);
        frame.dispose();
    }

    @Test
    public void verifyCanPerformGetMethodName() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();
        Field fullMethodButtonField = GRPCSamplerGui.class.
                getDeclaredField("fullMethodButton");
        Field fullMethodField = GRPCSamplerGui.class.
                getDeclaredField("fullMethodField");
        fullMethodField.setAccessible(true);
        fullMethodButtonField.setAccessible(true);
        JButton fullMethodButton = (JButton) fullMethodButtonField.get(grpRequestPluginGUI);
        JComboBox<String> fullMethodComboBox = (JComboBox<String>) fullMethodField.get(grpRequestPluginGUI);
        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString());
        grpcSampler.setLibFolder(LIB_FOLDER.toString());
        grpcSampler.setMetadata("dummyMetadata");
        grpcSampler.setHost("dummyHost");
        grpcSampler.setPort("dummyPort");
        grpcSampler.setFullMethod("");
        grpcSampler.setDeadline("500");
        grpcSampler.setTls(true);
        grpcSampler.setTlsDisableVerification(true);
        grpcSampler.setRequestJson("dummyRequest");
        grpRequestPluginGUI.configure(grpcSampler);
        fullMethodButton.doClick();
        Assert.assertEquals(fullMethodComboBox.getSelectedItem(), "bookstore.Bookstore/CreateShelf");
        Assert.assertNotNull(grpcSampler);
        Assert.assertNotNull(grpRequestPluginGUI);
        frame.dispose();
    }

    @Test
    public void verifyCantPerformGetMethodName() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();
        Field fullMethodButtonField = GRPCSamplerGui.class.
                getDeclaredField("fullMethodButton");
        Field fullMethodField = GRPCSamplerGui.class.
                getDeclaredField("fullMethodField");
        fullMethodField.setAccessible(true);
        fullMethodButtonField.setAccessible(true);
        JButton fullMethodButton = (JButton) fullMethodButtonField.get(grpRequestPluginGUI);
        JComboBox<String> fullMethodComboBox = (JComboBox<String>) fullMethodField.get(grpRequestPluginGUI);
        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder("");
        grpcSampler.setLibFolder(LIB_FOLDER.toString());
        grpcSampler.setMetadata("dummyMetadata");
        grpcSampler.setHost("dummyHost");
        grpcSampler.setPort("dummyPort");
        grpcSampler.setFullMethod("");
        grpcSampler.setDeadline("500");
        grpcSampler.setTls(true);
        grpcSampler.setTlsDisableVerification(true);
        grpcSampler.setRequestJson("dummyRequest");
        grpRequestPluginGUI.configure(grpcSampler);
        fullMethodButton.doClick();
        Assert.assertEquals(fullMethodComboBox.getSelectedItem(), "");
        Assert.assertNotNull(grpcSampler);
        Assert.assertNotNull(grpRequestPluginGUI);
        frame.dispose();
    }

    @Test
    public void testJSONMetadataGrpcTestElement() throws InterruptedException {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();
        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        GRPCSampler grpcSampler = (GRPCSampler) grpRequestPluginGUI.createTestElement();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder(PROTO_FOLDER.toString());
        grpcSampler.setLibFolder("");
        grpcSampler.setMetadata(METADATA_JSON);
        grpcSampler.setHost("localhost");
        grpcSampler.setPort("50051");
        grpcSampler.setFullMethod(FULL_METHOD_WITH_METADATA);
        grpcSampler.setDeadline("2000");
        grpcSampler.setTls(true);
        grpcSampler.setTlsDisableVerification(true);
        grpcSampler.setRequestJson(METADATA_REQUEST_JSON);
        grpRequestPluginGUI.configure(grpcSampler);
        Assert.assertNotNull(grpcSampler);
        Assert.assertNotNull(grpRequestPluginGUI);
        frame.dispose();
    }

    @Test
    public void testExceptionRequestWithListenerResultTree() throws Exception {
        // init window
        RenderAsText samplerResultTab = new RenderAsText();
        ViewResultsFullVisualizerGui viewResultsFullVisualizerGui = new ViewResultsFullVisualizerGui(samplerResultTab);
        JFrame frame = new JFrame("testExceptionRequestListenerResultTree");
        frame.setPreferredSize(new Dimension(1024, 768));
        Container contentPane = frame.getContentPane();
        contentPane.add(viewResultsFullVisualizerGui, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        // exec request
        HostAndPort hostAndPort = HostAndPort.fromString(HOST_PORT);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setName("testExceptionRequestListenerResultTree");
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder(PROTO_PATH_WITH_ERROR_IMPORT_FOLDER.toString());
        grpcSampler.setLibFolder(LIB_FOLDER.toString());
        grpcSampler.setMetadata(METADATA);
        grpcSampler.setHost(hostAndPort.getHost());
        grpcSampler.setPort(Integer.toString(hostAndPort.getPort()));
        grpcSampler.setFullMethod(FULL_METHOD_INVALID);
        grpcSampler.setDeadline("2000");
        grpcSampler.setTls(false);
        grpcSampler.setTlsDisableVerification(false);
        grpcSampler.setChannelShutdownAwaitTime("5000");
        grpcSampler.setRequestJson(REQUEST_JSON);
        grpcSampler.threadStarted();
        SampleResult sampleResult = grpcSampler.sample(null);

        // set result
        viewResultsFullVisualizerGui.add(sampleResult);
        Assert.assertEquals(sampleResult.getResponseCode(), " 400");
        Assert.assertEquals(sampleResult.getResponseMessage(), GrpcSamplerConstant.CLIENT_EXCEPTION_MSG);
        Assert.assertTrue(new String(sampleResult.getResponseData()).contains("invalid.proto:11:1: File recursively imports itself: invalid.proto -> invalid.proto"));

        // Destruction of the view
        frame.dispose();

        // Leave the view and wait indefinitely so that the JVM does not exit and the results are verified manually
//        Object lock = new Object();
//        while (true) {
//            synchronized (lock) {
//                lock.wait();
//            }
//        }
    }

}
