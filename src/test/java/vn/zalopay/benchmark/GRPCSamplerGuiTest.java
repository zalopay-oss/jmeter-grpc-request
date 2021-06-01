package vn.zalopay.benchmark;

import kg.apc.emulators.TestJMeterUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.swing.*;
import java.awt.*;


public class GRPCSamplerGuiTest {

    @BeforeClass
    public void setUpClass() {
        TestJMeterUtils.createJmeterEnv();
    }


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

}
