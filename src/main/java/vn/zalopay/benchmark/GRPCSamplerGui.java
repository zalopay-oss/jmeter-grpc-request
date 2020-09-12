package vn.zalopay.benchmark;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import kg.apc.jmeter.JMeterPluginsUtils;
import kg.apc.jmeter.gui.BrowseAction;
import kg.apc.jmeter.gui.GuiBuilderHelper;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public class GRPCSamplerGui extends AbstractSamplerGui {

  private static final long serialVersionUID = 240L;
  private static final String WIKIPAGE = "GRPCSampler";

  private JTextField protoFolderField;
  private JButton protoBrowseButton;

  private JTextField libFolderField;
  private JButton libBrowseButton;

  private JTextField fullMethodField;
  private JLabeledTextField metadataField;
  private JLabeledTextField hostField;
  private JLabeledTextField portField;
  private JLabeledTextField deadlineField;

  private JCheckBox isTLSCheckBox;

  private JSyntaxTextArea requestJsonArea;

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
      sampler.setHost(this.hostField.getText());
      sampler.setPort(this.portField.getText());
      sampler.setFullMethod(this.fullMethodField.getText());
      sampler.setDeadline(this.deadlineField.getText());
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
      hostField.setText(sampler.getHost());
      portField.setText(sampler.getPort());
      fullMethodField.setText(sampler.getFullMethod());
      deadlineField.setText(sampler.getDeadline());
      isTLSCheckBox.setSelected(sampler.isTls());
      requestJsonArea.setText(sampler.getRequestJson());
    }
  }

  @Override
  public void clearGui() {
    super.clearGui();
    initGuiValues();
  }

  private void initGuiValues() {
    protoFolderField.setText("");
    libFolderField.setText("");
    metadataField.setText("");
    hostField.setText("");
    portField.setText("");
    fullMethodField.setText("");
    deadlineField.setText("0");
    isTLSCheckBox.setSelected(false);
    requestJsonArea.setText("");
  }

  private void initGui() {
    setLayout(new BorderLayout(0, 5));
    setBorder(makeBorder());

    // TOP panel
    Container topPanel = makeTitlePanel();
    add(JMeterPluginsUtils.addHelpLinkToPanel(topPanel, WIKIPAGE), BorderLayout.NORTH);
    add(topPanel, BorderLayout.NORTH);

    // MAIN panel
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.add(getWebServerPanel());
    mainPanel.add(getGRPCRequestPanel());
    mainPanel.add(getOptionConfigPanel());
    mainPanel.add(getRequestJSONPanel());
    add(mainPanel, BorderLayout.CENTER);
  }

  /**
   * Helper function
   */

  private void addToPanel(JPanel panel, GridBagConstraints constraints, int col, int row,
      JComponent component) {
    constraints.gridx = col;
    constraints.gridy = row;
    panel.add(component, constraints);
  }

  private JPanel getRequestJSONPanel() {
    requestJsonArea = JSyntaxTextArea.getInstance(30, 50);// $NON-NLS-1$
    requestJsonArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);

    JPanel webServerPanel = new JPanel(new BorderLayout());
    webServerPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(9, 0, 0, 0),
        BorderFactory.createTitledBorder("Send JSON Format With the Request")
    ));
    webServerPanel.add(JTextScrollPane.getInstance(requestJsonArea));
    return webServerPanel;
  }

  private JPanel getOptionConfigPanel() {
    metadataField = new JLabeledTextField("Metadata:", 32); // $NON-NLS-1$
    deadlineField = new JLabeledTextField("Deadline:", 7); // $NON-NLS-1$

    JPanel webServerPanel = new HorizontalPanel();
    webServerPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(9, 0, 0, 0),
        BorderFactory.createTitledBorder("Optional Configuration")
    ));
    webServerPanel.add(metadataField);
    webServerPanel.add(deadlineField);
    return webServerPanel;
  }

  private JPanel getGRPCRequestPanel() {
    JPanel requestPanel = new JPanel(new GridBagLayout());

    GridBagConstraints labelConstraints = new GridBagConstraints();
    labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

    GridBagConstraints editConstraints = new GridBagConstraints();
    editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
    editConstraints.weightx = 1.0;
    editConstraints.fill = GridBagConstraints.HORIZONTAL;

    // Proto folder
    int row = 0;
    addToPanel(requestPanel, labelConstraints, 0, row,
        new JLabel("Proto Root Directory: ", JLabel.RIGHT));
    addToPanel(requestPanel, editConstraints, 1, row, protoFolderField = new JTextField(20));
    addToPanel(requestPanel, labelConstraints, 2, row,
        protoBrowseButton = new JButton("Browse..."));
    row++;
    GuiBuilderHelper.strechItemToComponent(protoFolderField, protoBrowseButton);

    editConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
    labelConstraints.insets = new java.awt.Insets(2, 0, 0, 0);

    protoBrowseButton.addActionListener(new BrowseAction(protoFolderField, true));

    // Lib folder
    addToPanel(requestPanel, labelConstraints, 0, row,
        new JLabel("Library Directory (Optional): ", JLabel.RIGHT));
    addToPanel(requestPanel, editConstraints, 1, row, libFolderField = new JTextField(20));
    addToPanel(requestPanel, labelConstraints, 2, row, libBrowseButton = new JButton("Browse..."));
    row++;
    GuiBuilderHelper.strechItemToComponent(libFolderField, libBrowseButton);

    editConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
    labelConstraints.insets = new java.awt.Insets(2, 0, 0, 0);

    libBrowseButton.addActionListener(new BrowseAction(libFolderField, true));

    // Full method
    addToPanel(requestPanel, labelConstraints, 0, row, new JLabel("Full Method: ", JLabel.RIGHT));
    addToPanel(requestPanel, editConstraints, 1, row, fullMethodField = new JTextField(20));

    JPanel container = new JPanel(new BorderLayout());
    container.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(9, 0, 0, 0),
        BorderFactory.createTitledBorder("GRPC Request")
    ));
    container.add(requestPanel, BorderLayout.NORTH);
    return container;
  }

  private JPanel getWebServerPanel() {
    portField = new JLabeledTextField("Port Number:", 7); // $NON-NLS-1$
    hostField = new JLabeledTextField("Server Name or IP:", 32); // $NON-NLS-1$
    isTLSCheckBox = new JCheckBox("SSL/TLS");

    JPanel webServerPanel = new HorizontalPanel();
    webServerPanel.setBorder(BorderFactory.createTitledBorder("Web Server")); // $NON-NLS-1$
    webServerPanel.add(hostField);
    webServerPanel.add(portField);
    webServerPanel.add(isTLSCheckBox);
    return webServerPanel;
  }

}
