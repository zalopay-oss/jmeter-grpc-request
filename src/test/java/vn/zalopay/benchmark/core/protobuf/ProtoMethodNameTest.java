package vn.zalopay.benchmark.core.protobuf;

import org.apache.logging.log4j.util.Strings;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProtoMethodNameTest {

    @Test
    public void testCanCheckInvalidMethodName() {
        Assert.assertEquals(ProtoMethodName.invalidMethodName(Strings.EMPTY), true);
        Assert.assertEquals(ProtoMethodName.invalidMethodName("     "), true);
        Assert.assertEquals(ProtoMethodName.invalidMethodName(null), true);
        Assert.assertEquals(ProtoMethodName.invalidMethodName("Service"), false);
        Assert.assertEquals(ProtoMethodName.invalidMethodName(""), true);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCantParseFullGrpcMethodNameWithErrorInExtractFullServiceName() {
        MockedStatic<io.grpc.MethodDescriptor> methodDescriptor =
                Mockito.mockStatic(io.grpc.MethodDescriptor.class);
        methodDescriptor
                .when(() -> io.grpc.MethodDescriptor.extractFullServiceName(Mockito.anyString()))
                .thenReturn("abc..\\/");
        ProtoMethodName.parseFullGrpcMethodName("dummyyyyyyyyy");
    }
}
