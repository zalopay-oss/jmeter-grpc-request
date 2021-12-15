package vn.zalopay.benchmark;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.protobuf.Descriptors;
import kg.apc.jmeter.JMeterPluginsUtils;
import kg.apc.jmeter.gui.BrowseAction;
import kg.apc.jmeter.gui.GuiBuilderHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.zalopay.benchmark.core.ClientList;
import vn.zalopay.benchmark.core.protobuf.ProtoMethodName;
import vn.zalopay.benchmark.core.protobuf.ServiceResolver;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;

public class GRPCSamplerGui extends AbstractSamplerGui {

    private static final Logger log = LoggerFactory.getLogger(GRPCSamplerGui.class);
    private static final long serialVersionUID = 240L;
    private static final String WIKIPAGE = "GRPCSampler";

    private JTextField protoFolderField;
    private JButton protoBrowseButton;

    private JTextField libFolderField;
    private JButton libBrowseButton;

    private JComboBox<String> fullMethodField;
    private JButton fullMethodButton;

    private JTextField metadataField;
    private JLabeledTextField hostField;
    private JLabeledTextField portField;
    private JLabeledTextField deadlineField;

    private JCheckBox isTLSCheckBox;
    private JCheckBox isTLSDisableVerificationCheckBox;

    private JSyntaxTextArea requestJsonArea;

    private ServiceResolver serviceResolver;

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
        if (!(element instanceof GRPCSampler)) {
            return;
        }
        GRPCSampler sampler = (GRPCSampler) element;
        sampler.setProtoFolder(this.protoFolderField.getText());
        sampler.setLibFolder(this.libFolderField.getText());
        sampler.setMetadata(this.metadataField.getText());
        sampler.setHost(this.hostField.getText());
        sampler.setPort(this.portField.getText());
        sampler.setFullMethod(this.fullMethodField.getSelectedItem().toString());
        sampler.setDeadline(this.deadlineField.getText());
        sampler.setTls(this.isTLSCheckBox.isSelected());
        sampler.setTlsDisableVerification(this.isTLSDisableVerificationCheckBox.isSelected());
        sampler.setRequestJson(this.requestJsonArea.getText());
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (!(element instanceof GRPCSampler)) {
            return;
        }
        GRPCSampler sampler = (GRPCSampler) element;
        protoFolderField.setText(sampler.getProtoFolder());
        libFolderField.setText(sampler.getLibFolder());
        metadataField.setText(sampler.getMetadata());
        hostField.setText(sampler.getHost());
        portField.setText(sampler.getPort());
        fullMethodField.setSelectedItem(sampler.getFullMethod());
        deadlineField.setText(sampler.getDeadline());
        isTLSCheckBox.setSelected(sampler.isTls());
        isTLSDisableVerificationCheckBox.setSelected(sampler.isTlsDisableVerification());
        requestJsonArea.setText(sampler.getRequestJson());
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
        fullMethodField.setSelectedItem("");
        deadlineField.setText("1000");
        isTLSCheckBox.setSelected(false);
        isTLSDisableVerificationCheckBox.setSelected(false);
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

    private JPanel getWebServerPanel() {
        portField = new JLabeledTextField("Port Number:", 3); // $NON-NLS-1$
        hostField = new JLabeledTextField("Server Name or IP:", 11); // $NON-NLS-1$
        isTLSCheckBox = new JCheckBox("SSL/TLS");
        isTLSDisableVerificationCheckBox = new JCheckBox("Disable SSL/TLS Cert Verification");
        JPanel webServerPanel = new VerticalPanel();
        webServerPanel.setBorder(BorderFactory.createTitledBorder("Web Server")); // $NON-NLS-1$

        JPanel webserverHostPanel = new HorizontalPanel();
        webserverHostPanel.add(hostField);
        webserverHostPanel.add(portField);

        JPanel webserverOtherPanel = new HorizontalPanel();
        webserverOtherPanel.add(isTLSCheckBox);
        webserverOtherPanel.add(isTLSDisableVerificationCheckBox);
        webServerPanel.add(webserverHostPanel);
        webServerPanel.add(webserverOtherPanel);
        return webServerPanel;
    }

    private JPanel getRequestJSONPanel() {
        requestJsonArea = JSyntaxTextArea.getInstance(30, 50);
        requestJsonArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);

        JPanel webServerPanel = new JPanel(new BorderLayout());
        webServerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(9, 0, 0, 0),
                BorderFactory.createTitledBorder("Send JSON Format With the Request")
        ));
        JTextScrollPane syntaxPanel = JTextScrollPane.getInstance(requestJsonArea);
        webServerPanel.add(syntaxPanel);
        return webServerPanel;
    }

    private JPanel getOptionConfigPanel() {
        JLabel metadataLabel = new JLabel("Metadata:");
        metadataField = new JTextField("Metadata", 32); // $NON-NLS-1$
        deadlineField = new JLabeledTextField("Deadline:", 7); // $NON-NLS-1$

        JPanel webServerPanel = new HorizontalPanel();
        webServerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(9, 0, 0, 0),
                BorderFactory.createTitledBorder("Optional Configuration")
        ));
        webServerPanel.add(metadataLabel);
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
        addToPanel(requestPanel, editConstraints, 1, row, fullMethodField = new JComboBox<>());
        fullMethodField.setEditable(true);
        addToPanel(requestPanel, labelConstraints, 2, row, fullMethodButton = new JButton("Listing..."));

        fullMethodButton.addActionListener(new ActionListener() {
            // fullMethodButton click listener
            @Override
            public void actionPerformed(ActionEvent e) {
                getMethods(fullMethodField);
            }
        });
        fullMethodField.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
            // fullMethod list checked listener
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                requestMock();
            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        fullMethodField.addActionListener(new ActionListener() {
            // fullMethod edit enter listener
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("comboBoxEdited".equals(e.getActionCommand())) {
                    requestMock();
                }
            }
        });

        // Container
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(9, 0, 0, 0),
                BorderFactory.createTitledBorder("GRPC Request")
        ));
        container.add(requestPanel, BorderLayout.NORTH);
        return container;
    }

    private void getMethods(JComboBox<String> fullMethodField) {
        String protoFolderText = protoFolderField.getText();
        if (StringUtils.isNotBlank(protoFolderText)) {
            serviceResolver = ClientList.getServiceResolver(protoFolderText, libFolderField.getText());
            List<String> methods = ClientList.listServices(serviceResolver);

            log.info("Full Methods: " + methods.toString());
            String[] methodsArr = new String[methods.size()];
            methods.toArray(methodsArr);

            fullMethodField.setModel(new DefaultComboBoxModel<>(methodsArr));
            try {
                Object selectedItem = fullMethodField.getSelectedItem();
                fullMethodField.setSelectedItem(selectedItem);
            } catch (Exception e) {
                fullMethodField.setSelectedIndex(0);
            }
        }
    }

    private void requestMock() {
        try {
            if (StringUtils.isNotBlank(requestJsonArea.getText())) {
                return;
            }
            String fullMethod = fullMethodField.getSelectedItem().toString();
            ProtoMethodName grpcMethodName = ProtoMethodName.parseFullGrpcMethodName(fullMethod);
            if (serviceResolver == null) {
                serviceResolver = ClientList.getServiceResolver(protoFolderField.getText(), libFolderField.getText());
            }
            Descriptors.MethodDescriptor methodDescriptor = serviceResolver.resolveServiceMethod(grpcMethodName);
            if (methodDescriptor != null) {
                Descriptors.Descriptor inputType = methodDescriptor.getInputType();
                List<Descriptors.FieldDescriptor> fields = inputType.getFields();
                JSONObject requestBody = new JSONObject(true);
                for (Descriptors.FieldDescriptor field : fields) {
                    String name = field.getName();
                    Object defaultValue = getValue(field);
                    requestBody.put(name, defaultValue);
                }
                String text = requestBody.toString(
                        SerializerFeature.PrettyFormat,         // Formatting Json String
                        SerializerFeature.WriteMapNullValue,    // Outputs Null values
                        SerializerFeature.WriteNullListAsEmpty  // Null List output is []
                );
                requestJsonArea.setText(text);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Object getValue(Descriptors.FieldDescriptor field) {
        String name = field.getName();
        String type = field.getType().name().toLowerCase();
        if ("message".equals(type)) {
            List<Descriptors.FieldDescriptor> fields = field.getMessageType().getFields();
            JSONObject repeatedField = new JSONObject(true);
            for (Descriptors.FieldDescriptor repeatedFieldDescriptor : fields) {
                repeatedField.put(repeatedFieldDescriptor.getName(), this.getValue(repeatedFieldDescriptor));
            }
            return repeatedField;
        } else {
            return getDefaultValue(name, type);
        }
    }

    private Object getDefaultValue(String name, String type) {
        switch (type) {
            case "string":
                return interpretMockViaFieldName(name);
            case "bool":
                return true;
            case "number":
            case "int32":
                return 10;
            case "int64":
                return 20;
            case "uint32":
            case "uint64":
            case "sint32":
                return 100;
            case "sint64":
                return 1200;
            case "fixed32":
                return 1400;
            case "fixed64":
                return 1500;
            case "sfixed32":
                return 1600;
            case "sfixed64":
                return 1700;
            case "float":
                return 1.1;
            case "double":
                return 1.4;
            case "bytes":
                return "Hello";
            default:
                return null;
        }
    }

    /**
     * Tries to guess a mock value from the field name.
     * Default Hello.
     */
    private String interpretMockViaFieldName(String fieldName) {
        String fieldNameLower = fieldName.toLowerCase();

        if (fieldNameLower.startsWith("id") || fieldNameLower.endsWith("id")) {
            return UUID.randomUUID().toString();
        }

        return "Hello";
    }

}
