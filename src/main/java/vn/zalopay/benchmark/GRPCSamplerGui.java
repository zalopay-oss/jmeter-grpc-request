package vn.zalopay.benchmark;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import kg.apc.jmeter.JMeterPluginsUtils;
import kg.apc.jmeter.gui.BrowseAction;
import kg.apc.jmeter.gui.GuiBuilderHelper;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

public class GRPCSamplerGui extends AbstractSamplerGui {

  private static final long serialVersionUID = 240L;
  public static final String WIKIPAGE = "GRPCSampler";

  private JTextField protoFolderField;
  private JButton browseButton;

  private JTextField libFolderField;
  private JTextField metadataField;
  private JTextField hostPortField;
  private JTextField requestFileField;
  private JTextField fullMethodField;
  private JTextField deadlineField;

  private JCheckBox isTLSCheckBox;

  private JTextArea requestJsonArea;

  public GRPCSamplerGui() {
    super();
    initGui();
    initGuiValues();
  }

  @Override
  public String getLabelResource() {
    return "grpc_sampler_title"; // $NON-NLS-1$
  }

  @Override
  public String getStaticLabel() {
    return "GRPC Request";
  }

  @Override
  public TestElement createTestElement() {
    GRPCSampler sampler = new GRPCSampler();
    modifyTestElement(sampler);
    return sampler;
  }

  @Override
  public void modifyTestElement(TestElement element) {
    configureTestElement(element);

    if (element instanceof GRPCSampler) {
      GRPCSampler sampler = (GRPCSampler) element;

      sampler.setProtoFolder(this.protoFolderField.getText());
      sampler.setLibFolder(this.libFolderField.getText());
      sampler.setMetadata(this.metadataField.getText());
      sampler.setHostPort(this.hostPortField.getText());
      sampler.setRequestFile(this.requestFileField.getText());
      sampler.setFullMethod(this.fullMethodField.getText());
      sampler.setDeadline(Long.parseLong(this.deadlineField.getText()));
      sampler.setTls(this.isTLSCheckBox.isSelected());
      sampler.setRequestJson(this.requestJsonArea.getText());
    }
  }

  @Override
  public void configure(TestElement element) {
    super.configure(element);

    if (element instanceof GRPCSampler) {
      GRPCSampler sampler = (GRPCSampler) element;

      protoFolderField.setText(sampler.getProtoFolder());
      libFolderField.setText(sampler.getLibFolder());
      metadataField.setText(sampler.getMetadata());
      hostPortField.setText(sampler.getHostPort());
      requestFileField.setText(sampler.getRequestFile());
      fullMethodField.setText(sampler.getFullMethod());
      deadlineField.setText(String.valueOf(sampler.getDeadline()));
      isTLSCheckBox.setSelected(sampler.isTls());
      requestJsonArea.setText(sampler.getRequestJson());
    }
  }

  @Override
  public void clearGui() {
    super.clearGui();
    initGuiValues();
  }

  /**
   * Helper function
   */

  private void initGuiValues() {
    protoFolderField.setText("");
    libFolderField.setText("");
    metadataField.setText("");
    hostPortField.setText("");
    requestFileField.setText("");
    fullMethodField.setText("");
    deadlineField.setText("0");
    isTLSCheckBox.setSelected(false);
    requestJsonArea.setText("");
  }

  private void addToPanel(JPanel panel, GridBagConstraints constraints, int col, int row,
      JComponent component) {
    constraints.gridx = col;
    constraints.gridy = row;
    panel.add(component, constraints);
  }

  private void initGui() {
    setLayout(new BorderLayout(0, 5));
    setBorder(makeBorder());

    Container topPanel = makeTitlePanel();

    add(JMeterPluginsUtils.addHelpLinkToPanel(topPanel, WIKIPAGE), BorderLayout.NORTH);
    add(topPanel, BorderLayout.NORTH);

    JPanel mainPanel = new JPanel(new GridBagLayout());

    GridBagConstraints labelConstraints = new GridBagConstraints();
    labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

    GridBagConstraints editConstraints = new GridBagConstraints();
    editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
    editConstraints.weightx = 1.0;
    editConstraints.fill = GridBagConstraints.HORIZONTAL;

    // Proto folder
    int row = 0;
    addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Proto Folder: ", JLabel.RIGHT));
    addToPanel(mainPanel, editConstraints, 1, row, protoFolderField = new JTextField(20));
    addToPanel(mainPanel, labelConstraints, 2, row, browseButton = new JButton("Browse..."));
    row++;
    GuiBuilderHelper.strechItemToComponent(protoFolderField, browseButton);

    editConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
    labelConstraints.insets = new java.awt.Insets(2, 0, 0, 0);

    browseButton.addActionListener(new BrowseAction(protoFolderField, true));

    // Host port
    addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Host Port: ", JLabel.RIGHT));
    addToPanel(mainPanel, editConstraints, 1, row, hostPortField = new JTextField(20));
    row++;

    // Full method
    addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Full Method: ", JLabel.RIGHT));
    addToPanel(mainPanel, editConstraints, 1, row, fullMethodField = new JTextField(20));
    row++;

    // Lib folder
    addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Lib Folder: ", JLabel.RIGHT));
    addToPanel(mainPanel, editConstraints, 1, row, libFolderField = new JTextField(20));
    row++;

    // Request file
    addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Request File: ", JLabel.RIGHT));
    addToPanel(mainPanel, editConstraints, 1, row, requestFileField = new JTextField(20));
    row++;

    // Metadata
    addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Metadata: ", JLabel.RIGHT));
    addToPanel(mainPanel, editConstraints, 1, row, metadataField = new JTextField(20));
    row++;

    // Deadline
    addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Deadline: ", JLabel.RIGHT));
    addToPanel(mainPanel, editConstraints, 1, row, deadlineField = new JTextField(20));
    row++;

    // TLS
    addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("TLS: ", JLabel.RIGHT));
    addToPanel(mainPanel, editConstraints, 1, row, isTLSCheckBox = new JCheckBox());
    row++;

    editConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
    labelConstraints.insets = new java.awt.Insets(4, 0, 0, 2);

    editConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
    labelConstraints.insets = new java.awt.Insets(4, 0, 0, 2);

    addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Request JSON: ", JLabel.RIGHT));

    labelConstraints.insets = new java.awt.Insets(4, 0, 0, 0);

    requestJsonArea = new JTextArea();
    addToPanel(mainPanel, editConstraints, 1, row,
        GuiBuilderHelper.getTextAreaScrollPaneContainer(requestJsonArea, 10));

    JPanel container = new JPanel(new BorderLayout());
    container.add(mainPanel, BorderLayout.NORTH);
    add(container, BorderLayout.CENTER);
  }
}
