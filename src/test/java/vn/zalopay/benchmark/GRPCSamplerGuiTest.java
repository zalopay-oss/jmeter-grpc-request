package vn.zalopay.benchmark;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import javax.swing.JDialog;
import javax.swing.WindowConstants;
import kg.apc.emulators.TestJMeterUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class GRPCSamplerGuiTest {

  @BeforeClass
  public static void setUpClass() {
    TestJMeterUtils.createJmeterEnv();
  }

  @Test
  public void showGui() throws Exception {
    if (!GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance()) {
      GRPCSamplerGui gui = new GRPCSamplerGui();
      JDialog frame = new JDialog();
      frame.add(gui);

      frame.setPreferredSize(new Dimension(800, 600));
      frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
      while (frame.isVisible()) {
        Thread.sleep(100);
      }
    }
  }
}
