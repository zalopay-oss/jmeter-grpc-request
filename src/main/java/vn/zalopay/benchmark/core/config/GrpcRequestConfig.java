package vn.zalopay.benchmark.core.config;


public class GrpcRequestConfig {
    private String hostPort;
    private String testProtoFile;
    private String libFolder;
    private String fullMethod;
    private boolean tls;
    private boolean tlsDisableVerification;
    private int awaitTerminationTimeout;

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

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public String getTestProtoFile() {
        return testProtoFile;
    }

    public void setTestProtoFile(String testProtoFile) {
        this.testProtoFile = testProtoFile;
    }

    public String getLibFolder() {
        return libFolder;
    }

    public void setLibFolder(String libFolder) {
        this.libFolder = libFolder;
    }

    public String getFullMethod() {
        return fullMethod;
    }

    public void setFullMethod(String fullMethod) {
        this.fullMethod = fullMethod;
    }

    public boolean isTls() {
        return tls;
    }

    public void setTls(boolean tls) {
        this.tls = tls;
    }

    public boolean isTlsDisableVerification() {
        return tlsDisableVerification;
    }

    public void setTlsDisableVerification(boolean tlsDisableVerification) {
        this.tlsDisableVerification = tlsDisableVerification;
    }

    public int getAwaitTerminationTimeout() {
        return awaitTerminationTimeout;
    }

    public void setAwaitTerminationTimeout(String awaitTerminationTimeout) {
        try {
            this.awaitTerminationTimeout = convertAwaitTerminationTimeout(awaitTerminationTimeout);
        } catch (NumberFormatException e) {
            this.awaitTerminationTimeout = 5000;
        }
    }

    private int convertAwaitTerminationTimeout(String awaitTerminationTimeout) {
        try {
            return Integer.parseInt(awaitTerminationTimeout);
        } catch (NumberFormatException e) {
            return 5000;
        }
    }

    @Override
    public String toString() {
        return "GrpcRequestConfig{" +
                "hostPort='" + hostPort + '\'' +
                ", testProtoFile='" + testProtoFile + '\'' +
                ", libFolder='" + libFolder + '\'' +
                ", fullMethod='" + fullMethod + '\'' +
                ", tls=" + tls +
                ", tlsDisableVerification=" + tlsDisableVerification +
                ", awaitTerminationTimeout=" + awaitTerminationTimeout +
                '}';
    }
}
