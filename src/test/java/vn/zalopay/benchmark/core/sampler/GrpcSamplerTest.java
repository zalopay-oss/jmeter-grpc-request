package vn.zalopay.benchmark.core.sampler;

import com.google.common.net.HostAndPort;
import com.google.protobuf.util.JsonFormat;

import org.apache.jmeter.samplers.SampleResult;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import vn.zalopay.benchmark.GRPCSampler;
import vn.zalopay.benchmark.core.BaseTest;
import vn.zalopay.benchmark.core.ClientCaller;
import vn.zalopay.benchmark.core.message.Writer;
import vn.zalopay.benchmark.core.specification.GrpcResponse;

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
        grpcSampler.setChannelShutdownAwaitTime("5000");
        grpcSampler.setRequestJson(REQUEST_JSON);
        grpcSampler.threadStarted();
        SampleResult sampleResult = grpcSampler.sample(null);
        Assert.assertEquals(sampleResult.getResponseCode(), "200");
        Assert.assertTrue(
                new String(sampleResult.getResponseData()).contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendSampleRequest3times() {
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
        grpcSampler.setChannelShutdownAwaitTime("5000");
        grpcSampler.setTlsDisableVerification(false);
        grpcSampler.setRequestJson(REQUEST_JSON);
        grpcSampler.threadStarted();
        SampleResult sampleResult1 = grpcSampler.sample(null);
        SampleResult sampleResult2 = grpcSampler.sample(null);
        SampleResult sampleResult3 = grpcSampler.sample(null);
        System.err.println(new String(sampleResult3.getResponseData()));
        Assert.assertEquals(sampleResult1.getResponseCode(), "200");
        Assert.assertTrue(
                new String(sampleResult1.getResponseData()).contains("\"theme\": \"Hello server"));
        Assert.assertEquals(sampleResult2.getResponseCode(), "200");
        Assert.assertTrue(
                new String(sampleResult2.getResponseData()).contains("\"theme\": \"Hello server"));
        Assert.assertEquals(sampleResult3.getResponseCode(), "200");
        Assert.assertTrue(
                new String(sampleResult3.getResponseData()).contains("\"theme\": \"Hello server"));
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
        Assert.assertTrue(
                new String(sampleResult.getResponseData()).contains("\"theme\": \"Hello server"));
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
        Assert.assertTrue(
                new String(sampleResult.getResponseData())
                        .contains("io.grpc.StatusRuntimeException: DEADLINE_EXCEEDED:"));
    }

    @Test
    public void testCanSendSampleRequestWithErrorNullResponse() {
        MockedStatic<Writer> writerSatic = Mockito.mockStatic(Writer.class);
        ClientCaller clientCaller = Mockito.mock(ClientCaller.class);
        Writer writer = Mockito.mock(Writer.class);
        Mockito.doNothing().when(writer).onError(Mockito.any(Throwable.class));
        Mockito.doNothing().when(writer).onNext(Mockito.any(com.google.protobuf.Message.class));
        Mockito.when(clientCaller.call("500")).thenThrow(new RuntimeException("Dummy Exception"));
        writerSatic
                .when(
                        () ->
                                Writer.create(
                                        Mockito.any(GrpcResponse.class),
                                        Mockito.any(JsonFormat.TypeRegistry.class)))
                .thenAnswer((i) -> writer);
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
        Assert.assertTrue(
                new String(sampleResult.getResponseData())
                        .contains("io.grpc.StatusRuntimeException: DEADLINE_EXCEEDED"));
    }

    @Test
    public void testCanCloseChanelTwiceWhenThreadFinishedTrigger2Times() {
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
        grpcSampler.threadFinished();
        Assert.assertEquals(sampleResult.getResponseCode(), "200");
        Assert.assertTrue(
                new String(sampleResult.getResponseData()).contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testSampleRequestWithJsonMetadata() {
        HostAndPort hostAndPort = HostAndPort.fromString("localhost:50051");
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder(PROTO_FOLDER.toString());
        grpcSampler.setLibFolder("");
        grpcSampler.setMetadata(METADATA_JSON);
        grpcSampler.setHost(hostAndPort.getHost());
        grpcSampler.setPort(Integer.toString(hostAndPort.getPort()));
        grpcSampler.setFullMethod(FULL_METHOD_WITH_METADATA);
        grpcSampler.setDeadline("2000");
        grpcSampler.setTls(false);
        grpcSampler.setTlsDisableVerification(false);
        grpcSampler.setChannelShutdownAwaitTime("5000");
        grpcSampler.setRequestJson(METADATA_REQUEST_JSON);
        grpcSampler.threadStarted();
        SampleResult sampleResult = grpcSampler.sample(null);
        Assert.assertEquals(sampleResult.getResponseCode(), "200");
        Assert.assertTrue(
                new String(sampleResult.getResponseData()).contains(EXPECTED_RESPONSE_DATA));
    }
}
