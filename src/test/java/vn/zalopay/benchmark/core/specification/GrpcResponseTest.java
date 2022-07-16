package vn.zalopay.benchmark.core.specification;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class GrpcResponseTest {

    @Test
    public void testGetGrpcResponseWithMultipleMessage() {
        Map<String, String> dummyMessage = new HashMap<>();
        dummyMessage.put("data", "message");
        GrpcResponse grpcResponse = new GrpcResponse();
        grpcResponse.storeGrpcMessage(dummyMessage);
        grpcResponse.storeGrpcMessage(dummyMessage);
        Assert.assertEquals(
                grpcResponse.getGrpcMessageString(), "[{data=message}, {data=message}]");
    }
}
