package vn.zalopay.benchmark.core.grpc;

import com.google.common.net.HostAndPort;
import io.grpc.*;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Knows how to construct grpc channels.
 */
public class ChannelFactory {
    public static ChannelFactory create() {
        return new ChannelFactory();
    }

    public ChannelFactory() {
    }

    public Channel createChannel(HostAndPort endpoint, boolean tls) {
        NettyChannelBuilder nettyChannelBuilder = createChannelBuilder(endpoint, tls);
        return nettyChannelBuilder.build();
    }

    private NettyChannelBuilder createChannelBuilder(HostAndPort endpoint, boolean tls) {
        if (tls) {
            return NettyChannelBuilder.forAddress(endpoint.getHost(), endpoint.getPort())
                    .negotiationType(NegotiationType.TLS)
                    .intercept(metadataInterceptor());
        }
        
        return NettyChannelBuilder.forAddress(endpoint.getHost(), endpoint.getPort())
                .negotiationType(NegotiationType.PLAINTEXT)
                .intercept(metadataInterceptor());
    }

    private ClientInterceptor metadataInterceptor() {
        ClientInterceptor interceptor = new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
                    final io.grpc.MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, final Channel next) {
                return new ClientInterceptors.CheckedForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
                    @Override
                    protected void checkedStart(Listener<RespT> responseListener, Metadata headers)
                            throws StatusException {
                        for (String entry : new LinkedList<>(Collections.singletonList("a"))) {
                            Metadata.Key<String> key = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
                            headers.put(key, "Bearer UM_TOKEN");
                        }
                        delegate().start(responseListener, headers);
                    }
                };
            }
        };

        return interceptor;
    }

}
