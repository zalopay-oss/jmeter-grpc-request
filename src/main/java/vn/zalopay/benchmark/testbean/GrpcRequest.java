package vn.zalopay.benchmark.testbean;

import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.ThreadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.zalopay.benchmark.core.ClientCaller;

public class GrpcRequest extends AbstractSampler implements ThreadListener, TestBean {
    private static final Logger log = LoggerFactory.getLogger(GrpcRequest.class);

    private static final long serialVersionUID = 232L;

    private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
        Arrays.asList("org.apache.jmeter.config.gui.SimpleConfigGui"));

    private String metadata;
    private String libFolder;
    private String protoFolder;
    private String hostPort;
    private String requestFile;
    private String fullMethod;
    private String requestJson;
    private long deadline = 0L;
    private boolean tls = Boolean.FALSE;

    private transient ClientCaller clientCaller = null;

    private void init() {
        clientCaller = new ClientCaller(hostPort, protoFolder, libFolder, fullMethod, tls, metadata);
    }

    @Override
    public SampleResult sample(Entry ignored) {
        if(clientCaller == null)
            init();

        SampleResult res = new SampleResult();
        res.setSampleLabel(getName());
        String req = clientCaller.buildRequest(requestFile, requestJson);
        res.setSamplerData(req);
        res.sampleStart();
        try {
            try{
                DynamicMessage resp = clientCaller.call(deadline);

                try {
                    res.sampleEnd();
                    res.setSuccessful(true);
                    res.setResponseData(JsonFormat.printer().print(resp).getBytes());
                    res.setResponseMessage("Success");
                    res.setDataType(SampleResult.TEXT);
                    res.setResponseCodeOK();
                } catch (InvalidProtocolBufferException e) {
                    errorResult(res, e);
                }
            } catch (RuntimeException e){
                errorResult(res, e);
            }
        } catch (StatusRuntimeException e) {
            errorResult(res, e);
        }
        return res;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public boolean applies(ConfigTestElement configElement) {
        String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
        return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
    }

    @Override
    public void threadStarted() {
        log.debug("{}\ttestStarted", whoAmI());
        // Nothing to do on thread start
    }

    @Override
    public void threadFinished() {
        log.debug("{}\ttestEnded", whoAmI());

        if (clientCaller != null) {
            clientCaller.shutdown();
        }
    }

    private String whoAmI() {
        return Thread.currentThread().getName() +
            "@" +
            Integer.toHexString(hashCode()) +
            "-" +
            getName();
    }

    private void errorResult(SampleResult res, Exception e) {
        res.sampleEnd();
        res.setSuccessful(false);
        res.setResponseMessage("Exception: " + e.getCause());
        res.setResponseData(e.getMessage().getBytes());
        res.setDataType(SampleResult.TEXT);
        res.setResponseCode("500");
    }

    /**
     * GETTER AND SETTER
     */

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getLibFolder() {
        return libFolder;
    }

    public void setLibFolder(String libFolder) {
        this.libFolder = libFolder;
    }

    public String getProtoFolder() {
        return protoFolder;
    }

    public void setProtoFolder(String protoFolder) {
        this.protoFolder = protoFolder;
    }

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public String getRequestFile() {
        return requestFile;
    }

    public void setRequestFile(String requestFile) {
        this.requestFile = requestFile;
    }

    public String getFullMethod() {
        return fullMethod;
    }

    public void setFullMethod(String fullMethod) {
        this.fullMethod = fullMethod;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public boolean isTls() {
        return tls;
    }

    public void setTls(boolean tls) {
        this.tls = tls;
    }
}
