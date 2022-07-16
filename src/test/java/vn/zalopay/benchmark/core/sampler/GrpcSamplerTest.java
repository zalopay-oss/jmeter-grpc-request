package vn.zalopay.benchmark.core.sampler;

import com.google.common.net.HostAndPort;
import com.google.protobuf.util.JsonFormat;

import org.apache.jmeter.samplers.SampleResult;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import vn.zalopay.benchmark.GRPCSampler;
import vn.zalopay.benchmark.constant.GrpcSamplerConstant;
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
        String responseData = new String(sampleResult.getResponseData());
        Assert.assertEquals(sampleResult.getResponseCode(), "200");
        Assert.assertTrue(
                responseData.contains("\"theme\": \"Hello server"),
                String.format(
                        "Actual: [%s] %n Expected: [%s]",
                        responseData, "\"theme\": \"Hello server"));
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
        String responseData1 = new String(sampleResult1.getResponseData());
        String responseData2 = new String(sampleResult2.getResponseData());
        String responseData3 = new String(sampleResult3.getResponseData());

        Assert.assertEquals(sampleResult1.getResponseCode(), "200");
        Assert.assertTrue(
                responseData1.contains("\"theme\": \"Hello server"),
                String.format(
                        "Actual: [%s] %n Expected: [%s]",
                        responseData1, "\"theme\": \"Hello server"));
        Assert.assertEquals(sampleResult2.getResponseCode(), "200");
        Assert.assertTrue(
                responseData2.contains("\"theme\": \"Hello server"),
                String.format(
                        "Actual: [%s] %n Expected: [%s]",
                        responseData2, "\"theme\": \"Hello server"));
        Assert.assertEquals(sampleResult3.getResponseCode(), "200");
        Assert.assertTrue(
                responseData3.contains("\"theme\": \"Hello server"),
                String.format(
                        "Actual: [%s] %n Expected: [%s]",
                        responseData3, "\"theme\": \"Hello server"));
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
        String responseData = new String(sampleResult.getResponseData());
        Assert.assertEquals(sampleResult.getResponseCode(), "200");
        Assert.assertTrue(
                responseData.contains("\"theme\": \"Hello server"),
                String.format(
                        "Actual: [%s] %n Expected: [%s]",
                        responseData, "\"theme\": \"Hello server"));
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
        Assert.assertEquals(sampleResult.getResponseCode(), " 500");
        Assert.assertTrue(
                sampleResult.getResponseMessage().contains("4 DEADLINE_EXCEEDED"),
                String.format(
                        "Actual: [%s] %n Expected: [%s]",
                        sampleResult.getResponseMessage(), "4 DEADLINE_EXCEEDED"));
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
        String responseData = new String(sampleResult.getResponseData());
        Assert.assertEquals(sampleResult.getResponseCode(), " 500");
        Assert.assertTrue(
                responseData.contains(" The stack trace is null"),
                String.format(
                        "Actual: [%s] %n Expected: [%s]",
                        responseData, " The stack trace is null"));
    }

    @Test
    public void testCanSendSampleRequestWithErrorProtoFolder() {
        HostAndPort hostAndPort = HostAndPort.fromString(HOST_PORT);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder(PROTO_PATH_WITH_ERROR_IMPORT_FOLDER.toString());
        grpcSampler.setLibFolder(LIB_FOLDER.toString());
        grpcSampler.setMetadata(METADATA);
        grpcSampler.setHost(hostAndPort.getHost());
        grpcSampler.setPort(Integer.toString(hostAndPort.getPort()));
        grpcSampler.setFullMethod(FULL_METHOD_INVALID);
        grpcSampler.setDeadline("2000");
        grpcSampler.setTls(false);
        grpcSampler.setTlsDisableVerification(false);
        grpcSampler.setChannelShutdownAwaitTime("5000");
        grpcSampler.setRequestJson(REQUEST_JSON);
        grpcSampler.threadStarted();
        SampleResult sampleResult = grpcSampler.sample(null);
        Assert.assertEquals(sampleResult.getResponseCode(), " 400");
        Assert.assertEquals(
                sampleResult.getResponseMessage(), GrpcSamplerConstant.CLIENT_EXCEPTION_MSG);
        String responseData = new String(sampleResult.getResponseData());
        Assert.assertTrue(
                responseData.contains(
                        "java.lang.RuntimeException: Unable to resolve service by invoking"
                                + " protoc:"),
                String.format(
                        "Actual: [%s] %n Expected: [%s]",
                        responseData,
                        "java.lang.RuntimeException: Unable to resolve service by invoking"
                                + " protoc:"));
    }

    @Test
    public void testCanSendSampleRequestWithErrorFullMethod() {
        HostAndPort hostAndPort = HostAndPort.fromString(HOST_PORT);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setComment("dummyComment");
        grpcSampler.setProtoFolder(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString());
        grpcSampler.setLibFolder(LIB_FOLDER.toString());
        grpcSampler.setMetadata(METADATA);
        grpcSampler.setHost(hostAndPort.getHost());
        grpcSampler.setPort(Integer.toString(hostAndPort.getPort()));
        grpcSampler.setFullMethod(FULL_METHOD_INVALID);
        grpcSampler.setDeadline("2000");
        grpcSampler.setTls(false);
        grpcSampler.setTlsDisableVerification(false);
        grpcSampler.setChannelShutdownAwaitTime("5000");
        grpcSampler.setRequestJson(REQUEST_JSON);
        grpcSampler.threadStarted();
        SampleResult sampleResult = grpcSampler.sample(null);
        String responseData = new String(sampleResult.getResponseData());
        Assert.assertEquals(sampleResult.getResponseCode(), " 400");
        Assert.assertEquals(
                sampleResult.getResponseMessage(), GrpcSamplerConstant.CLIENT_EXCEPTION_MSG);
        Assert.assertTrue(
                responseData.contains("Unable to find method Invalid in service Bookstore"),
                String.format(
                        "Actual: [%s] %n Expected: [%s]",
                        responseData, "Unable to find method Invalid in service Bookstore"));
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
        String responseData = new String(sampleResult.getResponseData());
        Assert.assertEquals(sampleResult.getResponseCode(), "200");
        Assert.assertTrue(
                responseData.contains("\"theme\": \"Hello server"),
                String.format(
                        "Actual: [%s] %n Expected: [%s]",
                        responseData, "\"theme\": \"Hello server"));
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
        String responseData = new String(sampleResult.getResponseData());
        Assert.assertEquals(sampleResult.getResponseCode(), "200");
        Assert.assertTrue(
                responseData.contains(EXPECTED_RESPONSE_DATA),
                String.format(
                        "Actual: [%s] %n Expected: [%s]", responseData, EXPECTED_RESPONSE_DATA));
    }
}
