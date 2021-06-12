package vn.zalopay.benchmark.core;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.core.protobuf.ServiceResolver;

import java.util.LinkedList;
import java.util.List;

public class ClientList {

    public static List<String> listServices(String protoFile, String libFolder) {
        List<String> methods = new LinkedList<>();

        final DescriptorProtos.FileDescriptorSet fileDescriptorSet;
        try {
            ProtocInvoker invoker = ProtocInvoker.forConfig(protoFile, libFolder);
            fileDescriptorSet = invoker.invoke();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to resolve service by invoking protoc", t);
        }

        ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
        for (ServiceDescriptor descriptor : serviceResolver.listServices()) {
            for (MethodDescriptor method : descriptor.getMethods()) {
                methods.add(descriptor.getFullName() + "/" + method.getName());
            }
        }

        return methods;
    }

}
