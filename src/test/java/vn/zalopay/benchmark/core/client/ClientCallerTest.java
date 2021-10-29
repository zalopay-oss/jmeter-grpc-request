package vn.zalopay.benchmark.core.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.netty.GrpcSslContexts;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.SslContextBuilder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import vn.zalopay.benchmark.core.BaseTest;
import vn.zalopay.benchmark.core.ClientCaller;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.core.specification.GrpcResponse;

import javax.net.ssl.SSLException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.any;

public class ClientCallerTest extends BaseTest {


    @Test
    public void testCanSendGrpcUnaryRequest() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1:1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("5000");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanGetShutDownBoolean() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1:1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        Assert.assertEquals(clientCaller.isShutdown(), false);
        Assert.assertEquals(clientCaller.isTerminated(), false);
    }

    @Test
    public void testCanGetShutDownBooleanAfterShutdown() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1:1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        clientCaller.shutdownNettyChannel();
        Assert.assertEquals(clientCaller.isShutdown(), true);
        Assert.assertEquals(clientCaller.isTerminated(), true);
    }

    @Test
    public void testCanCallClientStreamingRequest() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1:1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callClientStreaming("5000");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanCallServerStreamingRequest() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1:1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callServerStreaming("5000");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanCallBidiStreamingRequest() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1:1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callBidiStreaming("5000");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendRequestWithNegativeTimeoutRequest() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("-10");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Metadata entry must be defined in key1:value1,key2:value2 format: key1=1,key2:2")
    public void testCanThrowExceptionWithInvalidMetaData() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "key1=1,key2:2");
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("2000");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendGrpcUnaryRequestWithMetaData() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("2000");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendGrpcUnaryRequestWithEncodedMetaData() throws UnsupportedEncodingException {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, "tracestate:" + URLEncoder.encode("a=3,b:4", StandardCharsets.UTF_8.name()));
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("2000");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendGrpcUnaryRequestWithSSLAndDisableSSLVerification() {
        clientCaller = new ClientCaller(HOST_PORT_TLS, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, true, true, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10000");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test
    public void testCanSendGrpcUnaryRequestWithSSLAndEnableSSLVerification() {
        clientCaller = new ClientCaller(HOST_PORT_TLS, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, true, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10000");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing deadline to long")
    public void testThrowExceptionWithInvalidTimeoutFormat() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        clientCaller.call("1000s");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing deadline to long")
    public void testThrowExceptionWithBlankTimeoutFormat() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        clientCaller.call(" ");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing deadline to long")
    public void testThrowExceptionWithEmptyTimeoutFormat() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        clientCaller.call("");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing deadline to long")
    public void testThrowExceptionWithNullTimeoutFormat() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        clientCaller.call(null);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing request for rpc")
    public void testThrowExceptionWithInvalidRequestJson() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest("{shelf:{\"id\":1599156420811,\"theme\":\"Hello server!!\".}}");
        clientCaller.call("1000");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while parsing request for rpc")
    public void testThrowExceptionWithParsingRequestToJson() {
        MockedStatic<com.google.protobuf.util.JsonFormat> jsonFormat = Mockito.mockStatic(com.google.protobuf.util.JsonFormat.class);
        jsonFormat.when(JsonFormat::printer).then((i) -> new InvalidProtocolBufferException("Dummy Exception"));
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest("{shelf:{\"id\":1599156420811,\"theme\":\"Hello server!!\".}}");
        clientCaller.call("1000");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unable to resolve service by invoking protoc")
    public void testThrowExceptionWithExceptionInProtocInvoke() {
        MockedStatic<ProtocInvoker> protocInvoker = Mockito.mockStatic(ProtocInvoker.class);
        protocInvoker.when(() -> ProtocInvoker.forConfig(Mockito.anyString(), Mockito.anyString()).invoke()).thenThrow(new RuntimeException("Dummy Exception"));
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Error in create SSL connection!")
    public void testCanThrowExceptionWithSSLException() {
        MockedStatic<io.grpc.netty.GrpcSslContexts> grpcSslContextBuilder = Mockito.mockStatic(io.grpc.netty.GrpcSslContexts.class);
        grpcSslContextBuilder.when(() -> GrpcSslContexts.forClient()
        ).then(invocation -> {
                    SslContextBuilder sslContext = Mockito.mock(SslContextBuilder.class);
                    Mockito.when(sslContext.applicationProtocolConfig(any(ApplicationProtocolConfig.class))).then(i -> sslContext);
                    Mockito.when(sslContext.build()).thenThrow(new SSLException("Dummy Exception"));
                    return sslContext;
                }
        );
        clientCaller = new ClientCaller("localhost:1231", PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, true, false, METADATA);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Error in create SSL connection!")
    public void testCanThrowExceptionWithSSLExceptionAndDisableSSLVerification() {
        MockedStatic<io.grpc.netty.GrpcSslContexts> grpcSslContextBuilder = Mockito.mockStatic(io.grpc.netty.GrpcSslContexts.class);
        grpcSslContextBuilder.when(() -> GrpcSslContexts.forClient()
        ).then(invocation -> {
                    SslContextBuilder sslContext = Mockito.mock(SslContextBuilder.class);
                    Mockito.when(sslContext.applicationProtocolConfig(any(ApplicationProtocolConfig.class))).then(i -> sslContext);
                    Mockito.when(sslContext.build()).thenThrow(new SSLException("Dummy Exception"));
                    return sslContext;
                }
        );
        clientCaller = new ClientCaller("localhost:1231", PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, true, true, METADATA);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while waiting for rpc")
    public void testThrowExceptionWithTimeoutRequest() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), FULL_METHOD, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("1");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while waiting for rpc")
    public void testThrowExceptionWithTimeoutRequestServerStream() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstore.Bookstore/GetShelfStreamServer", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callServerStreaming("1");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while waiting for rpc")
    public void testThrowExceptionWithTimeoutRequestClientStream() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstore.Bookstore/GetShelfStreamClient", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callClientStreaming("1");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Caught exception while waiting for rpc")
    public void testThrowExceptionWithTimeoutRequestBidiStream() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstore.Bookstore/GetShelfStreamBidi", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.callBidiStreaming("1");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unable to find method invalidName in service Bookstore")
    public void testThrowExceptionWithInvalidMethodName() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstore.Bookstore/invalidName", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
    }

    @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "fullMethodName")
    public void testThrowExceptionWithNullMethodName() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), null, false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Could not extract full service from  ")
    public void testThrowExceptionWithBlankMethodName() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), " ", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Could not extract full service from ")
    public void testThrowExceptionWithEmptyMethodName() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Could not extract package name from bookstoreBookstore")
    public void testThrowExceptionWithPackageAndServiceMethodName() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstoreBookstore/CreateShelf", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Could not extract service from bookstoreBookstore.")
    public void testThrowExceptionWithInvalidPackagedName() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstoreBookstore./CreateShelf", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Could not extract method name from bookstore.Bookstore/")
    public void testThrowExceptionWithInvalidMethodNameWithDoubleSlash() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstore.Bookstore/", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
        GrpcResponse resp = clientCaller.call("10");
        clientCaller.shutdownNettyChannel();
        Assert.assertNotNull(resp);
        Assert.assertTrue(resp.getGrpcMessageString().contains("\"theme\": \"Hello server"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Unable to find service with name: Bookstores")
    public void testThrowExceptionWithInvalidServiceName() {
        clientCaller = new ClientCaller(HOST_PORT, PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString(), "bookstore.Bookstores/CreateShelf", false, false, METADATA);
        clientCaller.buildRequest(REQUEST_JSON);
    }
}
