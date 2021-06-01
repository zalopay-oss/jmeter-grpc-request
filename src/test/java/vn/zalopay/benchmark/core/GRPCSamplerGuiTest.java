package vn.zalopay.benchmark.core;

import org.apache.jmeter.sampler.DebugSampler;
import org.testng.Assert;
import org.testng.annotations.Test;
import vn.zalopay.benchmark.GRPCSampler;
import vn.zalopay.benchmark.GRPCSamplerGui;

import javax.swing.*;
import java.awt.*;


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
        frame.dispose();
    }
}
