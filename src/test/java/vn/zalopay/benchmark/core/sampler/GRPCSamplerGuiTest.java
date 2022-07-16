package vn.zalopay.benchmark.core.sampler;

import com.google.common.net.HostAndPort;

import org.apache.jmeter.gui.util.JSyntaxTextArea;
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

import java.awt.*;
import java.lang.reflect.Field;

import javax.swing.*;

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
    public void verifyCanPerformGetMethodName()
            throws NoSuchFieldException, IllegalAccessException {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();

        Field fullMethodButtonField = GRPCSamplerGui.class.getDeclaredField("fullMethodButton");
        Field fullMethodField = GRPCSamplerGui.class.getDeclaredField("fullMethodField");
        Field protoFolderField = GRPCSamplerGui.class.getDeclaredField("protoFolderField");
        Field libFolderField = GRPCSamplerGui.class.getDeclaredField("libFolderField");

        fullMethodField.setAccessible(true);
        fullMethodButtonField.setAccessible(true);
        protoFolderField.setAccessible(true);
        libFolderField.setAccessible(true);

        JButton fullMethodButton = (JButton) fullMethodButtonField.get(grpRequestPluginGUI);
        JTextField protoFolder = (JTextField) protoFolderField.get(grpRequestPluginGUI);
        JTextField libFolder = (JTextField) libFolderField.get(grpRequestPluginGUI);
        JComboBox<String> fullMethodComboBox =
                (JComboBox<String>) fullMethodField.get(grpRequestPluginGUI);
        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setMetadata("dummyMetadata");
        grpcSampler.setHost("dummyHost");
        grpcSampler.setPort("dummyPort");
        grpcSampler.setFullMethod("");
        grpcSampler.setDeadline("500");
        grpcSampler.setTls(true);
        grpcSampler.setTlsDisableVerification(true);
        grpcSampler.setRequestJson("dummyRequest");
        grpRequestPluginGUI.configure(grpcSampler);
        protoFolder.setText(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString());
        libFolder.setText(LIB_FOLDER.toString());
        fullMethodButton.doClick();
        Assert.assertEquals(
                fullMethodComboBox.getSelectedItem(), "bookstore.Bookstore/CreateShelf");
        Assert.assertNotNull(grpcSampler);
        Assert.assertNotNull(grpRequestPluginGUI);
        frame.dispose();
    }

    @Test
    public void verifyCanPerformGetMethodNameWithNotReload()
            throws NoSuchFieldException, IllegalAccessException {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();

        Field fullMethodButtonField = GRPCSamplerGui.class.getDeclaredField("fullMethodButton");
        Field fullMethodField = GRPCSamplerGui.class.getDeclaredField("fullMethodField");
        Field protoFolderField = GRPCSamplerGui.class.getDeclaredField("protoFolderField");
        Field libFolderField = GRPCSamplerGui.class.getDeclaredField("libFolderField");

        fullMethodField.setAccessible(true);
        fullMethodButtonField.setAccessible(true);
        protoFolderField.setAccessible(true);
        libFolderField.setAccessible(true);

        JButton fullMethodButton = (JButton) fullMethodButtonField.get(grpRequestPluginGUI);
        JTextField protoFolder = (JTextField) protoFolderField.get(grpRequestPluginGUI);
        JTextField libFolder = (JTextField) libFolderField.get(grpRequestPluginGUI);
        JComboBox<String> fullMethodComboBox =
                (JComboBox<String>) fullMethodField.get(grpRequestPluginGUI);
        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setMetadata("dummyMetadata");
        grpcSampler.setHost("dummyHost");
        grpcSampler.setPort("dummyPort");
        grpcSampler.setFullMethod("");
        grpcSampler.setDeadline("500");
        grpcSampler.setTls(true);
        grpcSampler.setTlsDisableVerification(true);
        grpcSampler.setRequestJson("dummyRequest");
        grpRequestPluginGUI.configure(grpcSampler);
        protoFolder.setText(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString());
        libFolder.setText(LIB_FOLDER.toString());
        fullMethodButton.doClick();
        fullMethodButton.doClick();
        Assert.assertEquals(
                fullMethodComboBox.getSelectedItem(), "bookstore.Bookstore/CreateShelf");
        Assert.assertNotNull(grpcSampler);
        Assert.assertNotNull(grpRequestPluginGUI);
        frame.dispose();
    }

    @Test(
            expectedExceptions = RuntimeException.class,
            expectedExceptionsMessageRegExp =
                    "Unable to resolve service by invoking protoc. The proto folder path is empty")
    public void verifyCantPerformGetMethodName()
            throws NoSuchFieldException, IllegalAccessException {
        JFrame frame = new JFrame("Test");
        try {
            GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();
            Field fullMethodButtonField = GRPCSamplerGui.class.getDeclaredField("fullMethodButton");
            Field fullMethodField = GRPCSamplerGui.class.getDeclaredField("fullMethodField");
            fullMethodField.setAccessible(true);
            fullMethodButtonField.setAccessible(true);
            JButton fullMethodButton = (JButton) fullMethodButtonField.get(grpRequestPluginGUI);
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
        } catch (RuntimeException e) {
            throw e;
        } finally {
            frame.dispose();
        }
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
    public void verifyCanPerformGenerateDummyRequest()
            throws NoSuchFieldException, IllegalAccessException {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();

        Field fullMethodButtonField = GRPCSamplerGui.class.getDeclaredField("fullMethodButton");
        Field fullMethodField = GRPCSamplerGui.class.getDeclaredField("fullMethodField");
        Field protoFolderField = GRPCSamplerGui.class.getDeclaredField("protoFolderField");
        Field libFolderField = GRPCSamplerGui.class.getDeclaredField("libFolderField");
        Field requestJsonAreaField = GRPCSamplerGui.class.getDeclaredField("requestJsonArea");

        fullMethodField.setAccessible(true);
        fullMethodButtonField.setAccessible(true);
        protoFolderField.setAccessible(true);
        libFolderField.setAccessible(true);
        requestJsonAreaField.setAccessible(true);

        JButton fullMethodButton = (JButton) fullMethodButtonField.get(grpRequestPluginGUI);
        JTextField protoFolder = (JTextField) protoFolderField.get(grpRequestPluginGUI);
        JTextField libFolder = (JTextField) libFolderField.get(grpRequestPluginGUI);
        JComboBox<String> fullMethodComboBox =
                (JComboBox<String>) fullMethodField.get(grpRequestPluginGUI);
        JSyntaxTextArea requestJsonArea =
                (JSyntaxTextArea) requestJsonAreaField.get(grpRequestPluginGUI);

        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setMetadata("dummyMetadata");
        grpcSampler.setHost("dummyHost");
        grpcSampler.setPort("dummyPort");
        grpcSampler.setFullMethod("");
        grpcSampler.setDeadline("500");
        grpcSampler.setTls(true);
        grpcSampler.setTlsDisableVerification(true);
        grpRequestPluginGUI.configure(grpcSampler);
        protoFolder.setText(PROTO_FOLDER.toString());
        libFolder.setText(LIB_FOLDER.toString());
        fullMethodButton.doClick();
        fullMethodComboBox.setSelectedItem("helloworld.Greeter/SayHello");
        fullMethodComboBox.hidePopup();
        Assert.assertEquals(fullMethodComboBox.getSelectedItem(), "helloworld.Greeter/SayHello");
        Assert.assertNotNull(grpcSampler);
        Assert.assertNotNull(grpRequestPluginGUI);
        Assert.assertEquals(requestJsonArea.getText(), "{\n" + "\t\"name\":\"Hello\"\n" + "}");
        frame.dispose();
    }

    @Test
    public void verifyCanPerformGenerateDummyRequestWithMultipleTypes()
            throws NoSuchFieldException, IllegalAccessException {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();

        Field fullMethodButtonField = GRPCSamplerGui.class.getDeclaredField("fullMethodButton");
        Field fullMethodField = GRPCSamplerGui.class.getDeclaredField("fullMethodField");
        Field protoFolderField = GRPCSamplerGui.class.getDeclaredField("protoFolderField");
        Field libFolderField = GRPCSamplerGui.class.getDeclaredField("libFolderField");
        Field requestJsonAreaField = GRPCSamplerGui.class.getDeclaredField("requestJsonArea");

        fullMethodField.setAccessible(true);
        fullMethodButtonField.setAccessible(true);
        protoFolderField.setAccessible(true);
        libFolderField.setAccessible(true);
        requestJsonAreaField.setAccessible(true);

        JButton fullMethodButton = (JButton) fullMethodButtonField.get(grpRequestPluginGUI);
        JTextField protoFolder = (JTextField) protoFolderField.get(grpRequestPluginGUI);
        JTextField libFolder = (JTextField) libFolderField.get(grpRequestPluginGUI);
        JComboBox<String> fullMethodComboBox =
                (JComboBox<String>) fullMethodField.get(grpRequestPluginGUI);
        JSyntaxTextArea requestJsonArea =
                (JSyntaxTextArea) requestJsonAreaField.get(grpRequestPluginGUI);

        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setMetadata("dummyMetadata");
        grpcSampler.setHost("dummyHost");
        grpcSampler.setPort("dummyPort");
        grpcSampler.setFullMethod("");
        grpcSampler.setDeadline("500");
        grpcSampler.setTls(true);
        grpcSampler.setTlsDisableVerification(true);
        grpRequestPluginGUI.configure(grpcSampler);
        protoFolder.setText(PROTO_FOLDER.toString());
        libFolder.setText(LIB_FOLDER.toString());
        fullMethodButton.doClick();
        fullMethodComboBox.setSelectedItem("fullprototype.Greeter/SayHello");
        fullMethodComboBox.hidePopup();
        Assert.assertEquals(fullMethodComboBox.getSelectedItem(), "fullprototype.Greeter/SayHello");
        Assert.assertNotNull(grpcSampler);
        Assert.assertNotNull(grpRequestPluginGUI);
        Assert.assertEquals(
                requestJsonArea.getText(),
                "{\n"
                        + "\t\"name\":\"Hello\",\n"
                        + "\t\"number1\":10,\n"
                        + "\t\"number2\":20,\n"
                        + "\t\"number3\":1.1,\n"
                        + "\t\"bytedata\":\"Hello\",\n"
                        + "\t\"number4\":1600,\n"
                        + "\t\"number5\":1700,\n"
                        + "\t\"number6\":100,\n"
                        + "\t\"number7\":100,\n"
                        + "\t\"number8\":1.4,\n"
                        + "\t\"number9\":1600,\n"
                        + "\t\"number10\":1.1,\n"
                        + "\t\"check\":true,\n"
                        + "\t\"number11\":100,\n"
                        + "\t\"number12\":1200,\n"
                        + "\t\"number13\":1500,\n"
                        + "\t\"map\":[\n"
                        + "\t\t{\n"
                        + "\t\t\t\"key\":\"Hello\",\n"
                        + "\t\t\t\"value\":\"Hello\"\n"
                        + "\t\t}\n"
                        + "\t]\n"
                        + "}");
        frame.dispose();
    }

    @Test
    public void verifyCannotPerformGenerateDummyRequestWithExistingDataRequestInJSArea()
            throws NoSuchFieldException, IllegalAccessException {
        GRPCSamplerGui grpRequestPluginGUI = new GRPCSamplerGui();

        Field fullMethodButtonField = GRPCSamplerGui.class.getDeclaredField("fullMethodButton");
        Field fullMethodField = GRPCSamplerGui.class.getDeclaredField("fullMethodField");
        Field protoFolderField = GRPCSamplerGui.class.getDeclaredField("protoFolderField");
        Field libFolderField = GRPCSamplerGui.class.getDeclaredField("libFolderField");
        Field requestJsonAreaField = GRPCSamplerGui.class.getDeclaredField("requestJsonArea");

        fullMethodField.setAccessible(true);
        fullMethodButtonField.setAccessible(true);
        protoFolderField.setAccessible(true);
        libFolderField.setAccessible(true);
        requestJsonAreaField.setAccessible(true);

        JButton fullMethodButton = (JButton) fullMethodButtonField.get(grpRequestPluginGUI);
        JTextField protoFolder = (JTextField) protoFolderField.get(grpRequestPluginGUI);
        JTextField libFolder = (JTextField) libFolderField.get(grpRequestPluginGUI);
        JComboBox<String> fullMethodComboBox =
                (JComboBox<String>) fullMethodField.get(grpRequestPluginGUI);
        JSyntaxTextArea requestJsonArea =
                (JSyntaxTextArea) requestJsonAreaField.get(grpRequestPluginGUI);

        JFrame frame = new JFrame("Test");
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.getContentPane().add(grpRequestPluginGUI, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setMetadata("dummyMetadata");
        grpcSampler.setHost("dummyHost");
        grpcSampler.setPort("dummyPort");
        grpcSampler.setFullMethod("");
        grpcSampler.setDeadline("500");
        grpcSampler.setTls(true);
        grpcSampler.setTlsDisableVerification(true);
        grpRequestPluginGUI.configure(grpcSampler);
        protoFolder.setText(PROTO_FOLDER.toString());
        libFolder.setText(LIB_FOLDER.toString());
        fullMethodButton.doClick();
        fullMethodComboBox.setSelectedItem("helloworld.Greeter/SayHello");
        fullMethodComboBox.hidePopup();

        protoFolder.setText(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString());
        libFolder.setText(LIB_FOLDER.toString());
        fullMethodButton.doClick();
        fullMethodComboBox.setSelectedItem("bookstore.Bookstore/CreateShelf");
        fullMethodComboBox.hidePopup();
        Assert.assertEquals(
                fullMethodComboBox.getSelectedItem(), "bookstore.Bookstore/CreateShelf");
        Assert.assertEquals(requestJsonArea.getText(), "{\n" + "\t\"name\":\"Hello\"\n" + "}");
        frame.dispose();
    }

    public void testExceptionRequestWithListenerResultTree() throws Exception {
        // init window
        RenderAsText samplerResultTab = new RenderAsText();
        ViewResultsFullVisualizerGui viewResultsFullVisualizerGui =
                new ViewResultsFullVisualizerGui(samplerResultTab);
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
        Assert.assertEquals(
                sampleResult.getResponseMessage(), GrpcSamplerConstant.CLIENT_EXCEPTION_MSG);
        Assert.assertTrue(
                new String(sampleResult.getResponseData())
                        .contains(
                                "invalid.proto:11:1: File recursively imports itself: invalid.proto"
                                        + " -> invalid.proto"));

        // GUI observation: Destruction of the view
        frame.dispose();
    }
}
