package vn.zalopay.benchmark.core.message;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import vn.zalopay.benchmark.core.BaseTest;
import vn.zalopay.benchmark.core.protobuf.ProtoMethodName;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.core.protobuf.ServiceResolver;

public class ReaderTest extends BaseTest {

    @Test
    public void testCanReadData() {
        ProtocInvoker protocInvoker =
                ProtocInvoker.forConfig(PROTO_FOLDER.toAbsolutePath().toString(), "");
        DescriptorProtos.FileDescriptorSet fileDescriptorSet = protocInvoker.invoke();
        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
        ProtoMethodName grpcMethodName =
                ProtoMethodName.parseFullGrpcMethodName(FULL_METHOD_WITH_METADATA);
        Descriptors.MethodDescriptor methodDescriptor =
                serviceResolver.resolveServiceMethod(grpcMethodName);
        JsonFormat.TypeRegistry registry =
                JsonFormat.TypeRegistry.newBuilder()
                        .add(serviceResolver.listMessageTypes())
                        .build();
        ImmutableList<DynamicMessage> msg =
                Reader.create(methodDescriptor.getInputType(), "", registry).read();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(msg.size(), 1);
        msg.forEach(
                m -> {
                    softAssert.assertEquals(
                            m.toBuilder().build().getDescriptorForType().getFullName(),
                            "helloworld.HelloRequest");
                    softAssert.assertEquals(m.toBuilder().build().toString(), "");
                });
        softAssert.assertAll();
    }
}
