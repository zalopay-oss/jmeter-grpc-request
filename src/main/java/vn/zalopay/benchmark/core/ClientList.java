package vn.zalopay.benchmark.core;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;

import org.apache.commons.lang3.StringUtils;

import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.core.protobuf.ServiceResolver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClientList {

    private static final Map<String, ServiceResolver> serviceResolverMap = new HashMap<>();

    public static ServiceResolver getServiceResolver(String protoFile, String libFolder) {
        return getServiceResolver(protoFile, libFolder, false);
    }

    /**
     * Get Proto File ServiceResolver
     *
     * @param protoFile proto file root path
     * @param libFolder lib file path
     * @param reload reload not cache
     * @return proto file resolver
     */
    public static ServiceResolver getServiceResolver(
            String protoFile, String libFolder, boolean reload) {
        try {
            String serviceResolverKey = protoFile + libFolder;
            if (reload == false) {
                ServiceResolver serviceResolver = serviceResolverMap.get(serviceResolverKey);
                if (serviceResolver != null) {
                    return serviceResolver;
                }
            }

            if (StringUtils.isNotBlank(protoFile)) {
                final DescriptorProtos.FileDescriptorSet fileDescriptorSet;
                ProtocInvoker invoker = ProtocInvoker.forConfig(protoFile, libFolder);
                fileDescriptorSet = invoker.invoke();

                ServiceResolver serviceResolver =
                        ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
                serviceResolverMap.put(serviceResolverKey, serviceResolver);
                return serviceResolver;
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to resolve service by invoking protoc", e);
        }

        throw new RuntimeException("Unable to resolve service by invoking protoc");
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
        return listServices(getServiceResolver(protoFile, libFolder, true));
    }
}
