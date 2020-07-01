package vn.zalopay.benchmark.testbean;

import com.google.protobuf.DynamicMessage;
import vn.zalopay.benchmark.core.ClientCaller;
import io.grpc.StatusRuntimeException;
import lombok.Getter;
import lombok.Setter;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.TestElement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GrpcRequest extends AbstractSampler implements TestBean {
    private static final long serialVersionUID = 240L;

    private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
            Collections.singletonList("org.apache.jmeter.config.gui.SimpleConfigGui"));
    private ClientCaller clientCaller = null;

    private void init() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_FOLDER, LIB_FOLDER, FULL_METHOD, TLS);
    }

    @Override
    public SampleResult sample(Entry ignored) {
        if (Objects.isNull(clientCaller))
            init();

        SampleResult res = new SampleResult();
        res.setSampleLabel(getName());
        String req = clientCaller.buildRequest(REQUEST_FILE, REQUEST_JSON);
        res.setSamplerData(req);
        res.sampleStart();
        try {
            DynamicMessage resp = clientCaller.call(DEADLINE);

            res.sampleEnd();
            res.setSuccessful(true);
            res.setResponseData(resp.toString().getBytes());
            res.setResponseMessage("Success");
            res.setResponseCodeOK();
        } catch (StatusRuntimeException e) {
            res.sampleEnd();
            res.setSuccessful(false);
            res.setResponseMessage("Exception: " + e.getStatus().getCode().name());
            res.setResponseData(e.getMessage().getBytes());
            res.setDataType(SampleResult.TEXT);
            res.setResponseCode("500");
        }
        return res;
    }

    @Override
    public boolean applies(ConfigTestElement configElement) {
        String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
        return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
    }

    @Getter
    @Setter
    private String LIB_FOLDER = "";
    @Getter
    @Setter
    private String PROTO_FOLDER = "";
    @Getter
    @Setter
    private String HOST_PORT = "";
    @Getter
    @Setter
    private String REQUEST_FILE = "";
    @Getter
    @Setter
    private String FULL_METHOD = "";
    @Getter
    @Setter
    private long DEADLINE = 0L;
    @Getter
    @Setter
    private String REQUEST_JSON = "";
    @Getter
    @Setter
    private boolean TLS = Boolean.FALSE;

}
