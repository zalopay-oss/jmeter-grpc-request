package vn.zalopay.benchmark;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.ThreadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.zalopay.benchmark.core.ClientCaller;
import vn.zalopay.benchmark.core.config.GrpcRequestConfig;
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
    public static final String CHANNEL_SHUTDOWN_AWAIT_TIME = "GRPCSampler.channelAwaitTermination";
    public static final String CHANNEL_MAX_INBOUND_MESSAGE_SIZE = "GRPCSampler" +
            ".maxInboundMessageSize";
    public static final String CHANNEL_MAX_INBOUND_METADATA_SIZE = "GRPCSampler.maxInboundMetadataSize";
    private transient ClientCaller clientCaller;
    private GrpcRequestConfig grpcRequestConfig;

    public GRPCSampler() {
        super();
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
        log.debug("{} ({}) {} {}", threadName,
                getTitle(), s, this.toString());
    }

    private void initGrpcConfigRequest() {
        if (grpcRequestConfig == null)
            grpcRequestConfig = GrpcRequestConfig.builder().hostPort(getHostPort())
                    .protoFolder(getProtoFolder())
                    .libFolder(getLibFolder())
                    .fullMethod(getFullMethod())
                    .tls(isTls())
                    .tlsDisableVerification(isTlsDisableVerification())
                    .awaitTerminationTimeout(getChannelShutdownAwaitTime())
                    .maxInboundMessageSize(getChannelMaxInboundMessageSize())
                    .maxInboundMetadataSize(getChannelMaxInboundMetadataSize())
                    .build();
    }

    private void initGrpcClient() {
        if (clientCaller == null) {
            clientCaller = new ClientCaller(grpcRequestConfig);
        }
    }

    @Override
    public SampleResult sample(Entry ignored) {
        GrpcResponse grpcResponse = new GrpcResponse();
        SampleResult sampleResult = new SampleResult();

        // Console prints unknown exceptions - Intercepts unknown exceptions and sends them to the console to print instead of throwing the results into the GRPC response.
        if (!initGrpcRequestSampler(sampleResult)) {
            return sampleResult;
        }
        // Process a GRPC request sampler
        processGrpcRequestSampler(grpcResponse, sampleResult);

        return sampleResult;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void threadStarted() {
        log.debug("\ttestStarted: {}", whoAmI());
    }

    @Override
    public void threadFinished() {
        log.debug("\ttestEnded: {}", whoAmI());
        if (clientCaller != null) {
            clientCaller.shutdownNettyChannel();
            clientCaller = null;
        }
        // clear state of grpc config for rerun with new config in GUI mode
        if (grpcRequestConfig != null) {
            grpcRequestConfig = null;
        }
    }

    private String whoAmI() {
        return Thread.currentThread().getName() +
                "@" +
                Integer.toHexString(hashCode()) +
                "-" +
                getName();
    }

    private void generateErrorResultInInitGRPCRequest(SampleResult sampleResult, Exception e) {
        String msg = String.format("GRPCSampler Exception: An unknown exception occurred before the GRPC " +
                "request was initiated. %s", e.getCause().getMessage());
        sampleResult.setSampleLabel("Error When Initiated gRPC");
        sampleResult.setSuccessful(false);
        sampleResult.setResponseCode("500");
        sampleResult.setDataType(SampleResult.TEXT);
        sampleResult.setResponseData(msg.getBytes(StandardCharsets.UTF_8));
        sampleResult.setResponseMessage(msg);
    }

    private void generateErrorResult(GrpcResponse grpcResponse, SampleResult sampleResult, Exception e) {
        try {
            sampleResult.setSuccessful(false);
            sampleResult.setResponseCode("500");
            sampleResult.setDataType(SampleResult.TEXT);
            sampleResult.sampleEnd();
            sampleResult.setResponseMessage("Exception: " + e.getCause().getMessage());
            sampleResult.setResponseData(String.format("Exception: %s. %s", e.getCause().getMessage(), grpcResponse.getGrpcMessageString()), "UTF-8");
        } catch (Exception ex) {
            // Prints exceptions that occur before the request is initiated
            e.printStackTrace();
            log.error("GrpcMessage: {}", grpcResponse.getGrpcMessageString());
        }
    }

    private boolean initGrpcRequestSampler(SampleResult sampleResult) {
        try {
            initGrpcInCurrentThread(sampleResult);
        } catch (Exception e) {
            log.error("An unknown error occurred before the GRPC request was initiated, and the stack trace is as follows: ", e);
            startSamplerToStopIfCatchException(sampleResult);
            generateErrorResultInInitGRPCRequest(sampleResult, e);
            return false;
        }
        return true;
    }

    private void processGrpcRequestSampler(GrpcResponse grpcResponse, SampleResult sampleResult) {
        try {
            grpcResponse = clientCaller.call(getDeadline());
            sampleResult.sampleEnd();
            sampleResult.setSuccessful(true);
            sampleResult.setResponseCodeOK();
            sampleResult.setResponseMessage("Success");
            sampleResult.setDataType(SampleResult.TEXT);
            sampleResult.setResponseData(grpcResponse.getGrpcMessageString().getBytes(StandardCharsets.UTF_8));
        } catch (RuntimeException e) {
            startSamplerToStopIfCatchException(sampleResult);
            generateErrorResult(grpcResponse, sampleResult, e);
        }
    }

    private void initGrpcInCurrentThread(SampleResult sampleResult) {
        initGrpcConfigRequest();
        initGrpcClient();
        sampleResult.setSampleLabel(getName());
        String grpcRequest = clientCaller.buildRequestAndMetadata(getRequestJson(), getMetadata());
        sampleResult.setSamplerData(grpcRequest);
        sampleResult.setRequestHeaders(clientCaller.getMetadataString());
        sampleResult.sampleStart();
    }

    private void startSamplerToStopIfCatchException(SampleResult sampleResult) {
        if (sampleResult.getStartTime() == 0L)
            sampleResult.sampleStart();
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

    public int getChannelShutdownAwaitTime() {
        return getPropertyAsInt(CHANNEL_SHUTDOWN_AWAIT_TIME, 5000);
    }

    public void setChannelShutdownAwaitTime(String awaitShutdownTime) {
        setProperty(CHANNEL_SHUTDOWN_AWAIT_TIME, awaitShutdownTime);
    }


    public int getChannelMaxInboundMessageSize() {
        return getPropertyAsInt(CHANNEL_MAX_INBOUND_MESSAGE_SIZE, 4194304);
    }

    public void setChannelMaxInboundMessageSize(String channelMaxInboundMessageSize) {
        setProperty(CHANNEL_MAX_INBOUND_MESSAGE_SIZE, channelMaxInboundMessageSize);
    }

    public int getChannelMaxInboundMetadataSize() {
        return getPropertyAsInt(CHANNEL_MAX_INBOUND_METADATA_SIZE, 8192);
    }

    public void setChannelMaxInboundMetadataSize(String channelMaxInboundMetadataSize) {
        setProperty(CHANNEL_MAX_INBOUND_METADATA_SIZE, channelMaxInboundMetadataSize);
    }

    private String getHostPort() {
        return getHost() + ":" + getPort();
    }
}
