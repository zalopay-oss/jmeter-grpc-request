package vn.zalopay.benchmark.core.config;


import org.testng.Assert;
import org.testng.annotations.Test;

public class GrpcRequestConfigTest {
    @Test
    public void canConvertConfigObjectToString() {
        GrpcRequestConfig grpcRequestConfig = new GrpcRequestConfig("a", "b", "c", "d", true, true, 1000);
        String grpcRequestConfigString = grpcRequestConfig.toString();
        Assert.assertEquals(grpcRequestConfigString, "GrpcRequestConfig{maxInboundMessageSize=4194304, maxInboundMetadataSize=8192, hostPort='a', testProtoFile='b', libFolder='c', fullMethod='d', tls=true, tlsDisableVerification=true, awaitTerminationTimeout=1000}");
    }

    @Test
    public void canInitGrpcRequestConfig() {
        GrpcRequestConfig grpcRequestConfig = new GrpcRequestConfig();
        String grpcRequestConfigString = grpcRequestConfig.toString();
        Assert.assertNotNull(grpcRequestConfig);
        Assert.assertEquals(grpcRequestConfigString, "GrpcRequestConfig{maxInboundMessageSize=4194304, " +
                "maxInboundMetadataSize=8192, hostPort='null', testProtoFile='null', libFolder='null', " +
                "fullMethod='null', tls=false, tlsDisableVerification=false, awaitTerminationTimeout=5000}");
    }

    @Test
    public void canGetDefaultGrpcRequestConfig() {
        GrpcRequestConfig grpcRequestConfig = new GrpcRequestConfig();
        Assert.assertEquals(grpcRequestConfig.getMaxInboundMessageSize(), 4194304);
        Assert.assertEquals(grpcRequestConfig.getMaxInboundMetadataSize(), 8192);
        Assert.assertEquals(grpcRequestConfig.getAwaitTerminationTimeout(), 5000);
    }
}
