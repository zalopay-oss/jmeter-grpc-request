package vn.zalopay.benchmark.core.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class GrpcRequestConfig {
    private int maxInboundMessageSize = 4194304;
    private int maxInboundMetadataSize = 8192;
    private String hostPort;
    private String protoFolder;
    private String libFolder;
    private String fullMethod;
    private boolean tls;
    private boolean tlsDisableVerification;
    private int awaitTerminationTimeout = 5000;

    public GrpcRequestConfig() {}

    public GrpcRequestConfig(
            String hostPort,
            String testProtoFile,
            String libFolder,
            String fullMethod,
            boolean tls,
            boolean tlsDisableVerification,
            int awaitTerminationTimeout) {
        this.hostPort = hostPort;
        this.protoFolder = testProtoFile;
        this.libFolder = libFolder;
        this.fullMethod = fullMethod;
        this.tls = tls;
        this.tlsDisableVerification = tlsDisableVerification;
        this.awaitTerminationTimeout = awaitTerminationTimeout;
    }

    public String getHostPort() {
        return hostPort;
    }

    public String getProtoFolder() {
        return protoFolder;
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

    public int getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    public int getMaxInboundMetadataSize() {
        return maxInboundMetadataSize;
    }

    @Override
    public String toString() {
        return "GrpcRequestConfig{"
                + "maxInboundMessageSize="
                + maxInboundMessageSize
                + ", maxInboundMetadataSize="
                + maxInboundMetadataSize
                + ", hostPort='"
                + hostPort
                + '\''
                + ", testProtoFile='"
                + protoFolder
                + '\''
                + ", libFolder='"
                + libFolder
                + '\''
                + ", fullMethod='"
                + fullMethod
                + '\''
                + ", tls="
                + tls
                + ", tlsDisableVerification="
                + tlsDisableVerification
                + ", awaitTerminationTimeout="
                + awaitTerminationTimeout
                + '}';
    }
}
