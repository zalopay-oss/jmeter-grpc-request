package vn.zalopay.benchmark.core.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import vn.zalopay.benchmark.core.BaseTest;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.util.ExceptionUtils;

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
                    "\tat vn.zalopay.benchmark.core.protobuf.ProtocInvoker.protocInvokerErrorHandler(ProtocInvoker.java:295)\n"
                        + "\tat vn.zalopay.benchmark.core.protobuf.ProtocInvoker.invokeBinary(ProtocInvoker.java:171)\n"
                        + "\tat vn.zalopay.benchmark.core.protobuf.ProtocInvoker.invoke(ProtocInvoker.java:104)\n"
                        + "\tat vn.zalopay.benchmark.core.utils.ExceptionUtilsTest.testCanGetPrintExceptionToStrWithSpecificLineStackTrace(ExceptionUtilsTest.java:22)\n"
                        + "\tat jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-2)\n"
                        + "\tat jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n"
                        + "\tat jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n"
                        + "\tat java.lang.reflect.Method.invoke(Method.java:566)\n"
                        + "\tat org.testng.internal.invokers.MethodInvocationHelper.invokeMethod(MethodInvocationHelper.java:139)\n"
                        + "\tat org.testng.internal.invokers.TestInvoker.invokeMethod(TestInvoker.java:677)\n";
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
            String expectedMsg =
                    "\tat vn.zalopay.benchmark.core.protobuf.ProtocInvoker.protocInvokerErrorHandler(ProtocInvoker.java:295)\n"
                        + "\tat vn.zalopay.benchmark.core.protobuf.ProtocInvoker.invokeBinary(ProtocInvoker.java:171)\n"
                        + "\tat vn.zalopay.benchmark.core.protobuf.ProtocInvoker.invoke(ProtocInvoker.java:104)\n"
                        + "\tat vn.zalopay.benchmark.core.utils.ExceptionUtilsTest.testCanGetPrintExceptionToStrWithInputMoreThanMaxLineStackTrace(ExceptionUtilsTest.java:47)\n"
                        + "\tat jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-2)\n"
                        + "\tat jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n"
                        + "\tat jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n"
                        + "\tat java.lang.reflect.Method.invoke(Method.java:566)\n"
                        + "\tat org.testng.internal.invokers.MethodInvocationHelper.invokeMethod(MethodInvocationHelper.java:139)\n"
                        + "\tat org.testng.internal.invokers.TestInvoker.invokeMethod(TestInvoker.java:677)\n"
                        + "\tat org.testng.internal.invokers.TestInvoker.invokeTestMethod(TestInvoker.java:221)\n"
                        + "\tat org.testng.internal.invokers.MethodRunner.runInSequence(MethodRunner.java:50)\n"
                        + "\tat org.testng.internal.invokers.TestInvoker$MethodInvocationAgent.invoke(TestInvoker.java:962)\n"
                        + "\tat org.testng.internal.invokers.TestInvoker.invokeTestMethods(TestInvoker.java:194)\n"
                        + "\tat org.testng.internal.invokers.TestMethodWorker.invokeTestMethods(TestMethodWorker.java:148)\n"
                        + "\tat org.testng.internal.invokers.TestMethodWorker.run(TestMethodWorker.java:128)\n"
                        + "\tat java.util.ArrayList.forEach(ArrayList.java:1541)\n"
                        + "\tat org.testng.TestRunner.privateRun(TestRunner.java:806)\n"
                        + "\tat org.testng.TestRunner.run(TestRunner.java:601)\n"
                        + "\tat org.testng.SuiteRunner.runTest(SuiteRunner.java:433)\n"
                        + "\tat org.testng.SuiteRunner.runSequentially(SuiteRunner.java:427)\n"
                        + "\tat org.testng.SuiteRunner.privateRun(SuiteRunner.java:387)\n"
                        + "\tat org.testng.SuiteRunner.run(SuiteRunner.java:330)\n"
                        + "\tat org.testng.SuiteRunnerWorker.runSuite(SuiteRunnerWorker.java:52)\n"
                        + "\tat org.testng.SuiteRunnerWorker.run(SuiteRunnerWorker.java:95)\n"
                        + "\tat org.testng.TestNG.runSuitesSequentially(TestNG.java:1256)\n"
                        + "\tat org.testng.TestNG.runSuitesLocally(TestNG.java:1176)\n"
                        + "\tat org.testng.TestNG.runSuites(TestNG.java:1099)\n"
                        + "\tat org.testng.TestNG.run(TestNG.java:1067)\n";
            String exceptionMsg = ExceptionUtils.getPrintExceptionToStr(e, 100);
            Assert.assertTrue(
                    exceptionMsg.contains(expectedMsg),
                    String.format("Actual: [%s] %n Expected: [%s]", exceptionMsg, expectedMsg));
        }
    }
}
