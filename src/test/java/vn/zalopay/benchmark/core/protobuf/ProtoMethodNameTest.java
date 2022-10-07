package vn.zalopay.benchmark.core.protobuf;

import org.apache.logging.log4j.util.Strings;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

    @Test
    public void testCanParseGrpcMethodAndServiceNameWithoutPacakge() {
        ProtoMethodName protoMethodName =
                ProtoMethodName.parseFullGrpcMethodName("serviceWithoutPackage/ServiceMethod");

        Assert.assertEquals("serviceWithoutPackage", protoMethodName.getServiceName());
        Assert.assertEquals("ServiceMethod", protoMethodName.getMethodName());
        Assert.assertNull(protoMethodName.getPackageName());
    }

    @Test(expectedExceptions = InvocationTargetException.class)
    public void testCantInstanceNewObject()
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?>[] ctors = WellKnownTypes.class.getDeclaredConstructors();
        Assert.assertEquals(1, ctors.length, "Utility class should only have one constructor");
        Constructor<?> ctor = ctors[0];
        ctor.setAccessible(true);
        ctor.newInstance();
        Assert.fail("Utility class constructor should be private");
    }
}
