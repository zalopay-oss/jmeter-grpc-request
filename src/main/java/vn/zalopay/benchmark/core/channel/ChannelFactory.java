package vn.zalopay.benchmark.core.channel;

import io.grpc.*;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

import java.net.SocketAddress;
import java.util.Map;

public class ChannelFactory {
    public static ChannelFactory create() {
        return new ChannelFactory();
    }

    private ChannelFactory() {
    }

    public ManagedChannel createChannel(SocketAddress endpoint, boolean tls, Map<String, String> metadataHash) {
        ManagedChannelBuilder<?> managedChannelBuilder = createChannelBuilder(endpoint, tls, metadataHash);
        return managedChannelBuilder.build();
    }

    private ManagedChannelBuilder<?> createChannelBuilder(SocketAddress endpoint, boolean tls,
                                                          Map<String, String> metadataHash) {
        if (tls) {
            return NettyChannelBuilder.forAddress(endpoint).negotiationType(NegotiationType.TLS)
                    .intercept(metadataInterceptor(metadataHash));
        }

        return NettyChannelBuilder.forAddress(endpoint).negotiationType(NegotiationType.PLAINTEXT)
                .intercept(metadataInterceptor(metadataHash));
    }

    private ClientInterceptor metadataInterceptor(Map<String, String> metadataHash) {
        return new ClientInterceptor() {
            @Override
            public <T, R> ClientCall<T, R> interceptCall(final io.grpc.MethodDescriptor<T, R> method,
                                                         CallOptions callOptions, final Channel next) {
                return new ClientInterceptors.CheckedForwardingClientCall<T, R>(next.newCall(method, callOptions)) {
                    @Override
                    protected void checkedStart(Listener<R> responseListener, Metadata headers) {
                        for (Map.Entry<String, String> entry : metadataHash.entrySet()) {
                            Metadata.Key<String> key = Metadata.Key.of(entry.getKey(),
                                    Metadata.ASCII_STRING_MARSHALLER);
                            headers.put(key, entry.getValue());
                        }
                        delegate().start(responseListener, headers);
                    }
                };
            }
        };
    }
}
