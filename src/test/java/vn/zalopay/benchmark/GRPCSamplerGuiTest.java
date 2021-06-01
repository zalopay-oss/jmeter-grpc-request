package vn.zalopay.benchmark;

import kg.apc.emulators.TestJMeterUtils;
import org.apache.jmeter.util.JMeterUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.swing.*;
import java.awt.*;
import java.net.URISyntaxException;


public class GRPCSamplerGuiTest {

    @BeforeClass
    public static void setUpClass() throws URISyntaxException {
        //
    }

    @Test
    public void dummy() {
        System.out.println("2");
    }

    @Test
    public void testCanShowGui() {
        TestJMeterUtils.createJmeterEnv();
        if (!GraphicsEnvironment.isHeadless()) {
            JMeterUtils.setProperty("search_paths", System.getProperty("user.home") + "/.m2/repository/org/apache/jmeter/ApacheJMeter_core/5.4.1");
            System.out.println("2");
            JFrame frame = new JFrame("Test");

            frame.setPreferredSize(new Dimension(1024, 768));
            //frame.getContentPane().add(obj, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);
        }
        // GRPCSamplerGui gui = new GRPCSamplerGui();
//
//        JDialog frame = new JDialog();
//        frame.add(gui);
//
//        frame.setPreferredSize(new Dimension(800, 600));
//        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//        main.add(frame);


    }

}
