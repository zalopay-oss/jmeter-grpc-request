package vn.zalopay.benchmark.core.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import vn.zalopay.benchmark.core.BaseTest;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.util.ExceptionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ExceptionUtilsTest extends BaseTest {
    @Test
    public void testCanGetPrintExceptionToStrWithNullThrowable() {
        String exceptionMsg = ExceptionUtils.getPrintExceptionToStr(null, 1);
        Assert.assertEquals(exceptionMsg, "The stack trace is null");
    }

    @Test
    public void testCanGetPrintExceptionToStrWithSpecificLineStackTrace() {
        try {
            ProtocInvoker protocInvoker =
                    ProtocInvoker.forConfig(JMETER_PROPERTIES_FILE.toAbsolutePath().toString(), "");
            protocInvoker.invoke();
        } catch (Exception e) {
            String expectedMsg =
                    "\tat org.testng.internal.invokers.TestInvoker.invokeMethod(TestInvoker.java:677)\n";
            String exceptionMsg = ExceptionUtils.getPrintExceptionToStr(e, 10);
            Assert.assertTrue(
                    exceptionMsg.contains(expectedMsg),
                    String.format("Actual: [%s] %n Expected: [%s]", exceptionMsg, expectedMsg));
        }
    }

    @Test
    public void testCanGetPrintExceptionToStrWithInputMoreThanMaxLineStackTrace() {
        try {
            ProtocInvoker protocInvoker =
                    ProtocInvoker.forConfig(JMETER_PROPERTIES_FILE.toAbsolutePath().toString(), "");
            protocInvoker.invoke();
        } catch (Exception e) {
            String expectedMsg = "\tat org.testng.TestNG.run(TestNG.java:1067)\n";
            String exceptionMsg = ExceptionUtils.getPrintExceptionToStr(e, 100);
            Assert.assertTrue(
                    exceptionMsg.contains(expectedMsg),
                    String.format("Actual: [%s] %n Expected: [%s]", exceptionMsg, expectedMsg));
        }
    }

    @Test(expectedExceptions = InvocationTargetException.class)
    public void testCantInstanceNewObject()
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?>[] ctors = ExceptionUtils.class.getDeclaredConstructors();
        Assert.assertEquals(1, ctors.length, "Utility class should only have one constructor");
        Constructor<?> ctor = ctors[0];
        ctor.setAccessible(true);
        ctor.newInstance();
        Assert.fail("Utility class constructor should be private");
    }
}
