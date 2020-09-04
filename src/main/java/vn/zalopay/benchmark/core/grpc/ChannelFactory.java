package vn.zalopay.benchmark.core.grpc;

import com.google.common.net.HostAndPort;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import java.util.Map;

/**
 * Knows how to construct grpc channels.
 */
public class ChannelFactory {
    public static ChannelFactory create() {
        return new ChannelFactory();
    }

    private ChannelFactory() {
    }

    public ManagedChannel createChannel(HostAndPort endpoint, boolean tls, Map<String, String> metadataHash) {
        ManagedChannelBuilder managedChannelBuilder = createChannelBuilder(endpoint, tls, metadataHash);
        return managedChannelBuilder.build();
    }

    private ManagedChannelBuilder createChannelBuilder(HostAndPort endpoint, boolean tls, Map<String, String> metadataHash) {
        if (tls) {
            return NettyChannelBuilder.forAddress(endpoint.getHost(), endpoint.getPort())
                    .negotiationType(NegotiationType.TLS)
                    .intercept(metadataInterceptor(metadataHash));
        }

        return NettyChannelBuilder.forAddress(endpoint.getHost(), endpoint.getPort())
                .negotiationType(NegotiationType.PLAINTEXT)
                .intercept(metadataInterceptor(metadataHash));
    }

    private ClientInterceptor metadataInterceptor(Map<String, String> metadataHash) {
        return new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
                    final io.grpc.MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, final Channel next) {
                return new ClientInterceptors.CheckedForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
                    @Override
                    protected void checkedStart(Listener<RespT> responseListener, Metadata headers) {
                        for (Map.Entry<String, String> entry : metadataHash.entrySet()) {
                            Metadata.Key<String> key = Metadata.Key.of(entry.getKey(), Metadata.ASCII_STRING_MARSHALLER);
                            headers.put(key, entry.getValue());
                        }
                        delegate().start(responseListener, headers);
                    }
                };
            }
        };
    }

}
