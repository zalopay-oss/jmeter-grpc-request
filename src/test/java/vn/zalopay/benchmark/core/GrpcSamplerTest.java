package vn.zalopay.benchmark.core;

import com.google.common.net.HostAndPort;
import org.apache.jmeter.samplers.SampleResult;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import vn.zalopay.benchmark.GRPCSampler;

public class GrpcSamplerTest extends BaseTest {

    @Test
    public void testCanSendSampleRequest() {
        HostAndPort hostAndPort = HostAndPort.fromString(HOST_PORT);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString());
        grpcSampler.setLibFolder(LIB_FOLDER.toString());
        grpcSampler.setMetadata(METADATA);
        grpcSampler.setHost(hostAndPort.getHost());
        grpcSampler.setPort(Integer.toString(hostAndPort.getPort()));
        grpcSampler.setFullMethod(FULL_METHOD);
        grpcSampler.setDeadline("2000");
        grpcSampler.setTls(false);
        grpcSampler.setTlsDisableVerification(false);
        grpcSampler.setRequestJson(REQUEST_JSON);
        SampleResult sampleResult = grpcSampler.sample(null);
        Assert.assertEquals(sampleResult.getResponseCode(), "200");
        Assert.assertTrue(new String(sampleResult.getResponseData()).contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendSampleRequestWithThreadStart() {
        HostAndPort hostAndPort = HostAndPort.fromString(HOST_PORT);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString());
        grpcSampler.setLibFolder(LIB_FOLDER.toString());
        grpcSampler.setMetadata(METADATA);
        grpcSampler.setHost(hostAndPort.getHost());
        grpcSampler.setPort(Integer.toString(hostAndPort.getPort()));
        grpcSampler.setFullMethod(FULL_METHOD);
        grpcSampler.setDeadline("2000");
        grpcSampler.setTls(false);
        grpcSampler.setTlsDisableVerification(false);
        grpcSampler.setRequestJson(REQUEST_JSON);
        grpcSampler.threadStarted();
        SampleResult sampleResult = grpcSampler.sample(null);
        grpcSampler.threadFinished();
        Assert.assertEquals(sampleResult.getResponseCode(), "200");
        Assert.assertTrue(new String(sampleResult.getResponseData()).contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendSampleRequestWithError() {
        HostAndPort hostAndPort = HostAndPort.fromString(HOST_PORT);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString());
        grpcSampler.setLibFolder(LIB_FOLDER.toString());
        grpcSampler.setMetadata(METADATA);
        grpcSampler.setHost(hostAndPort.getHost());
        grpcSampler.setPort(Integer.toString(hostAndPort.getPort()));
        grpcSampler.setFullMethod(FULL_METHOD);
        grpcSampler.setDeadline("1");
        grpcSampler.setTls(false);
        grpcSampler.setTlsDisableVerification(false);
        grpcSampler.setRequestJson(REQUEST_JSON);
        grpcSampler.threadStarted();
        SampleResult sampleResult = grpcSampler.sample(null);
        grpcSampler.threadFinished();
        grpcSampler.clear();
        Assert.assertEquals(sampleResult.getResponseCode(), "500");
        Assert.assertTrue(new String(sampleResult.getResponseData()).contains("io.grpc.StatusRuntimeException: DEADLINE_EXCEEDED:"));
    }

    @Test
    public void testCanSendSampleRequestWithErrorNullResponse() {
        ClientCaller clientCaller = Mockito.mock(ClientCaller.class);
        Mockito.when(clientCaller.call("500")).thenThrow(new RuntimeException("Dummy Exception"));
        HostAndPort hostAndPort = HostAndPort.fromString(HOST_PORT);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString());
        grpcSampler.setLibFolder(LIB_FOLDER.toString());
        grpcSampler.setMetadata(METADATA);
        grpcSampler.setHost(hostAndPort.getHost());
        grpcSampler.setPort(Integer.toString(hostAndPort.getPort()));
        grpcSampler.setFullMethod(FULL_METHOD);
        grpcSampler.setDeadline("1");
        grpcSampler.setTls(false);
        grpcSampler.setTlsDisableVerification(false);
        grpcSampler.setRequestJson(REQUEST_JSON);
        grpcSampler.threadStarted();
        SampleResult sampleResult = grpcSampler.sample(null);
        grpcSampler.threadFinished();
        grpcSampler.clear();
        Assert.assertEquals(sampleResult.getResponseCode(), "500");
        Assert.assertTrue(new String(sampleResult.getResponseData()).contains("Exception: io.grpc.StatusRuntimeException"));
    }
}
