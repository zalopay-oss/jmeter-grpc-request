package vn.zalopay.benchmark;

import jodd.exception.ExceptionUtil;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.ThreadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.zalopay.benchmark.core.ClientCaller;
import vn.zalopay.benchmark.core.specification.GrpcResponse;

import java.nio.charset.StandardCharsets;

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

    public GRPCSampler() {
        trace("init GRPCSampler");
    }

    /**
     * @return a string for the sampleResult Title
     */
    private String getTitle() {
        return this.getName();
    }

    private void trace(String s) {
        String threadName = Thread.currentThread().getName();
        log.debug("{} ({}) {} {} {}", threadName,
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
                    isTlsDisableVerification());
        }
    }

    @Override
    public SampleResult sample(Entry ignored) {
        SampleResult sampleResult = new SampleResult();

        // Console prints unknown exceptions - Intercepts unknown exceptions and sends them to the console to print instead of throwing the results into the GRPC response.
        try {
            initGrpcClient();
            sampleResult.setSampleLabel(getName());
            String grpcRequest = clientCaller.buildRequestAndMetadata(getRequestJson(),getMetadata());
            sampleResult.setSamplerData(grpcRequest);
            sampleResult.setRequestHeaders(clientCaller.getMetadataString());
            sampleResult.sampleStart();
        } catch (Exception e) {
            log.error("An unknown error occurred before the GRPC request was initiated, and the stack trace is as follows: ", e);
            sampleResult.setSuccessful(false);
            sampleResult.setResponseCode("500");
            sampleResult.setDataType(SampleResult.TEXT);
            sampleResult.setResponseMessage("GRPCSampler Exception: An unknown exception occurred before the GRPC request was initiated. See the console print log for a detailed stack trace.");
            return sampleResult;
        }

        // Initiate a GRPC request
        GrpcResponse grpcResponse = clientCaller.call(getDeadline());
        sampleResult.sampleEnd();
        sampleResult.setDataType(SampleResult.TEXT);
        if (grpcResponse.isSuccess()) {
            sampleResult.setSuccessful(true);
            sampleResult.setResponseCodeOK();
            sampleResult.setResponseMessage("Success");
            sampleResult.setResponseData(grpcResponse.getGrpcMessageString().getBytes(StandardCharsets.UTF_8));
        } else {
            sampleResult.setSuccessful(false);
            sampleResult.setResponseCode("500");
            sampleResult.setResponseMessage("Exception: " + grpcResponse.getThrowable().getMessage());
            sampleResult.setResponseData(ExceptionUtil.exceptionStackTraceToString(grpcResponse.getThrowable()), "UTF-8");
        }

        return sampleResult;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void threadStarted() {
        log.debug("{}\ttestStarted", whoAmI());
    }

    @Override
    public void threadFinished() {
        log.debug("{}\ttestEnded", whoAmI());
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
