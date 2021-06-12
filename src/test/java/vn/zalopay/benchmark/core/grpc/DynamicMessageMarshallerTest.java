package vn.zalopay.benchmark.core.grpc;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import org.testng.annotations.Test;
import vn.zalopay.benchmark.core.BaseTest;
import vn.zalopay.benchmark.core.protobuf.ProtoMethodName;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.core.protobuf.ServiceResolver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DynamicMessageMarshallerTest extends BaseTest {

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unable to merge from the supplied input stream")
    public void testCanHandleIOExceptionWhenParse() throws FileNotFoundException, ProtocInvoker.ProtocInvocationException {
        InputStream input = new FileInputStream(JMETER_PROPERTIES_FILE.toString());
        ProtoMethodName grpcMethodName = ProtoMethodName.parseFullGrpcMethodName(FULL_METHOD);
        DescriptorProtos.FileDescriptorSet
                fileDescriptorSet = ProtocInvoker.forConfig(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString()).invoke();
        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
        Descriptors.MethodDescriptor methodDescriptor = serviceResolver.resolveServiceMethod(grpcMethodName);
        DynamicMessageMarshaller dynamicMessageMarshaller = new DynamicMessageMarshaller(methodDescriptor.getOutputType());
        dynamicMessageMarshaller.parse(input);
    }
}
