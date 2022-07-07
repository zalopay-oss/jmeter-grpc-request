package vn.zalopay.benchmark.core.config;


public class GrpcRequestConfig {
    private int maxInboundMessageSize = 4194304;
    private int maxInboundMetadataSize = 8192;
    private String hostPort;
    private String testProtoFile;
    private String libFolder;
    private String fullMethod;
    private boolean tls;
    private boolean tlsDisableVerification;
    private int awaitTerminationTimeout = 5000;

    public GrpcRequestConfig() {
    }

    public GrpcRequestConfig(String hostPort, String testProtoFile, String libFolder, String fullMethod, boolean tls, boolean tlsDisableVerification, String awaitTerminationTimeout) {
        this.hostPort = hostPort;
        this.testProtoFile = testProtoFile;
        this.libFolder = libFolder;
        this.fullMethod = fullMethod;
        this.tls = tls;
        this.tlsDisableVerification = tlsDisableVerification;
        this.awaitTerminationTimeout = convertAwaitTerminationTimeout(awaitTerminationTimeout);
    }


    public String getHostPort() {
        return hostPort;
    }

    public String getTestProtoFile() {
        return testProtoFile;
    }

    public String getLibFolder() {
        return libFolder;
    }

    public String getFullMethod() {
        return fullMethod;
    }

    public boolean isTls() {
        return tls;
    }

    public boolean isTlsDisableVerification() {
        return tlsDisableVerification;
    }

    public int getAwaitTerminationTimeout() {
        return awaitTerminationTimeout;
    }

    private int convertAwaitTerminationTimeout(String awaitTerminationTimeout) {
        try {
            return Integer.parseInt(awaitTerminationTimeout);
        } catch (NumberFormatException e) {
            return 5000;
        }
    }

    public int getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }


    public int getMaxInboundMetadataSize() {
        return maxInboundMetadataSize;
    }

    @Override
    public String toString() {
        return "GrpcRequestConfig{" +
                "maxInboundMessageSize=" + maxInboundMessageSize +
                ", maxInboundMetadataSize=" + maxInboundMetadataSize +
                ", hostPort='" + hostPort + '\'' +
                ", testProtoFile='" + testProtoFile + '\'' +
                ", libFolder='" + libFolder + '\'' +
                ", fullMethod='" + fullMethod + '\'' +
                ", tls=" + tls +
                ", tlsDisableVerification=" + tlsDisableVerification +
                ", awaitTerminationTimeout=" + awaitTerminationTimeout +
                '}';
    }
}
