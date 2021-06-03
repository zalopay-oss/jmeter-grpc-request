package vn.zalopay.benchmark;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.ThreadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.zalopay.benchmark.core.ClientCaller;
import vn.zalopay.benchmark.core.specification.GrpcResponse;
import vn.zalopay.benchmark.exception.ShortExceptionName;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class GRPCSampler extends AbstractSampler implements ThreadListener {

    private static final Logger log = LoggerFactory.getLogger(GRPCSampler.class);
    private static final long serialVersionUID = 232L;

    public static final String METADATA = "GRPCSampler.metadata";
    public static final String LIB_FOLDER = "GRPCSampler.libFolder";
    public static final String PROTO_FOLDER = "GRPCSampler.protoFolder";
    public static final String HOST = "GRPCSampler.host";
    public static final String PORT = "GRPCSampler.port";
    public static final String FULL_METHOD = "GRPCSampler.fullMethod";
    public static final String REQUEST_JSON = "GRPCSampler.requestJson";
    public static final String DEADLINE = "GRPCSampler.deadline";
    public static final String TLS = "GRPCSampler.tls";
    public static final String TLS_DISABLE_VERIFICATION = "GRPCSampler.tlsDisableVerification";

    private transient ClientCaller clientCaller = null;

    private static AtomicInteger classCount = new AtomicInteger(0); // keep track of classes created

    public GRPCSampler() {
        classCount.incrementAndGet();
        trace("GRPCSampler()");
    }

    /**
     * @return a string for the sampleResult Title
     */
    private String getTitle() {
        return this.getName();
    }

    private void trace(String s) {
        log.debug("{} ({}) {} {} {}", Thread.currentThread().getName(), classCount.get(),
                getTitle(), s, this.toString());
    }

    private void initGrpcClient() {
        if (clientCaller == null) {
            clientCaller = new ClientCaller(
                    getHostPort(),
                    getProtoFolder(),
                    getLibFolder(),
                    getFullMethod(),
                    isTls(),
                    isTlsDisableVerification(),
                    getMetadata());
        }
    }

    @Override
    public SampleResult sample(Entry ignored) {
        GrpcResponse grpcResponse = new GrpcResponse();
        SampleResult sampleResult = new SampleResult();
        try {
            initGrpcClient();
            sampleResult.setSampleLabel(getName());
            String grpcRequest = clientCaller.buildRequest(getRequestJson());
            sampleResult.setSamplerData(grpcRequest);
            sampleResult.sampleStart();
            grpcResponse = clientCaller.call(getDeadline());
            sampleResult.sampleEnd();
            sampleResult.setSuccessful(true);
            sampleResult.setResponseData(grpcResponse.getGrpcMessageString().getBytes(StandardCharsets.UTF_8));
            sampleResult.setResponseMessage("Success");
            sampleResult.setDataType(SampleResult.TEXT);
            sampleResult.setResponseCodeOK();
        } catch (RuntimeException e) {
            errorResult(grpcResponse, sampleResult, e);
        }
        return sampleResult;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void threadStarted() {
        log.info("{}\ttestStarted", whoAmI());
    }

    @Override
    public void threadFinished() {
        log.info("{}\ttestEnded", whoAmI());
        if (clientCaller != null) {
            clientCaller.shutdownNettyChannel();
            clientCaller = null;
        }
    }

    private String whoAmI() {
        return Thread.currentThread().getName() +
                "@" +
                Integer.toHexString(hashCode()) +
                "-" +
                getName();
    }

    private void errorResult(GrpcResponse grpcResponse, SampleResult sampleResult, Exception e) {
        String responseMessage = grpcResponse != null ? grpcResponse.getGrpcMessageString() : "";
        sampleResult.sampleEnd();
        sampleResult.setSuccessful(false);
        sampleResult.setResponseData(String.format("Exception: %s. %s", e.getCause().getMessage(), responseMessage), "UTF-8");
        sampleResult.setResponseMessage("Exception: " + ShortExceptionName.shortest(e.getCause().getMessage()));
        sampleResult.setDataType(SampleResult.TEXT);
        sampleResult.setResponseCode("500");
    }

    /**
     * GETTER AND SETTER
     */

    public String getMetadata() {
        return getPropertyAsString(METADATA);
    }

    public void setMetadata(String metadata) {
        setProperty(METADATA, metadata);
    }

    public String getLibFolder() {
        return getPropertyAsString(LIB_FOLDER);
    }

    public void setLibFolder(String libFolder) {
        setProperty(LIB_FOLDER, libFolder);
    }

    public String getProtoFolder() {
        return getPropertyAsString(PROTO_FOLDER);
    }

    public void setProtoFolder(String protoFolder) {
        setProperty(PROTO_FOLDER, protoFolder);
    }

    public String getFullMethod() {
        return getPropertyAsString(FULL_METHOD);
    }

    public void setFullMethod(String fullMethod) {
        setProperty(FULL_METHOD, fullMethod);
    }

    public String getRequestJson() {
        return getPropertyAsString(REQUEST_JSON);
    }

    public void setRequestJson(String requestJson) {
        setProperty(REQUEST_JSON, requestJson);
    }

    public String getDeadline() {
        return getPropertyAsString(DEADLINE);
    }

    public void setDeadline(String deadline) {
        setProperty(DEADLINE, deadline);
    }

    public boolean isTls() {
        return getPropertyAsBoolean(TLS);
    }

    public void setTls(boolean tls) {
        setProperty(TLS, tls);
    }

    public boolean isTlsDisableVerification() {
        return getPropertyAsBoolean(TLS_DISABLE_VERIFICATION);
    }

    public void setTlsDisableVerification(boolean tlsDisableVerification) {
        setProperty(TLS_DISABLE_VERIFICATION, tlsDisableVerification);
    }

    public String getHost() {
        return getPropertyAsString(HOST);
    }

    public void setHost(String host) {
        setProperty(HOST, host);
    }

    public String getPort() {
        return getPropertyAsString(PORT);
    }

    public void setPort(String port) {
        setProperty(PORT, port);
    }

    private String getHostPort() {
        return getHost() + ":" + getPort();
    }
}
