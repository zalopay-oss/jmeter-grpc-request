package vn.zalopay.benchmark.core.grpc;

import com.google.protobuf.DescriptorProtos;

import org.testng.Assert;
import org.testng.annotations.Test;

import vn.zalopay.benchmark.core.BaseTest;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;

public class ProtocInvokerTest extends BaseTest {

    @Test
    public void canGenerateFileDescriptorSet() throws ProtocInvoker.ProtocInvocationException {
        ProtocInvoker protocInvoker =
                ProtocInvoker.forConfig(
                        PROTO_FOLDER.toAbsolutePath().toString(),
                        LIB_FOLDER.toAbsolutePath().toString());
        DescriptorProtos.FileDescriptorSet fileDescriptorSet = protocInvoker.invoke();
        Assert.assertTrue(fileDescriptorSet.getFileCount() > 0);
    }

    @Test(
            expectedExceptions = ProtocInvoker.ProtocInvocationException.class,
            expectedExceptionsMessageRegExp = ".*[Missing input file].*")
    public void cannotGenerateFileDescriptorSet() throws ProtocInvoker.ProtocInvocationException {
        ProtocInvoker protocInvoker =
                ProtocInvoker.forConfig(JMETER_PROPERTIES_FILE.toAbsolutePath().toString(), "");
        protocInvoker.invoke();
    }
}
