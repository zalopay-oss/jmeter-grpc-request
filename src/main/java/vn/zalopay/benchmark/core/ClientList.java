package vn.zalopay.benchmark.core;

import com.google.common.base.Strings;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.core.protobuf.ServiceResolver;

import java.util.LinkedList;
import java.util.List;

public class ClientList {

    public static ServiceResolver getServiceResolver(String protoFile, String libFolder) {
        if (!Strings.isNullOrEmpty(protoFile)) {
            final DescriptorProtos.FileDescriptorSet fileDescriptorSet;
            try {
                ProtocInvoker invoker = ProtocInvoker.forConfig(protoFile, libFolder);
                fileDescriptorSet = invoker.invoke();
            } catch (Throwable t) {
                throw new RuntimeException("Unable to resolve service by invoking protoc", t);
            }

            ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
            return serviceResolver;
        }

        return null;
    }

    public static List<String> listServices(ServiceResolver serviceResolver) {
        List<String> methods = new LinkedList<>();
        for (ServiceDescriptor descriptor : serviceResolver.listServices()) {
            for (MethodDescriptor method : descriptor.getMethods()) {
                methods.add(descriptor.getFullName() + "/" + method.getName());
            }
        }

        return methods;
    }

    public static List<String> listServices(String protoFile, String libFolder) {
        return listServices(getServiceResolver(protoFile, libFolder));
    }

}
