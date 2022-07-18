package vn.zalopay.benchmark.core.constant;

import org.testng.Assert;
import org.testng.annotations.Test;

import vn.zalopay.benchmark.constant.GrpcSamplerConstant;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class GrpcSamplerConstantTest {

    @Test(expectedExceptions = InvocationTargetException.class)
    public void testCantInstanceNewObject()
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?>[] ctors = GrpcSamplerConstant.class.getDeclaredConstructors();
        Assert.assertEquals(1, ctors.length, "Constant class should only have one constructor");
        Constructor<?> ctor = ctors[0];
        ctor.setAccessible(true);
        ctor.newInstance();
        Assert.fail("Constant class constructor should be private");
    }

    @Test
    public void testCanGetConstantErrorMessage() {
        Assert.assertEquals(
                GrpcSamplerConstant.CLIENT_EXCEPTION_MSG,
                " GRPCSampler parsing exception: An unknown exception occurred before the GRPC"
                        + " request was initiated, See response body for the stack trace.");
    }
}
