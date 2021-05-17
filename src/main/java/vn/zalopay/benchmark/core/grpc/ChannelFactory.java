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
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.util.Map;
import javax.net.ssl.SSLException;


/**
 * Knows how to construct grpc channels.
 */
public class ChannelFactory {
    public static ChannelFactory create() {
        return new ChannelFactory();
    }

    private ChannelFactory() {
    }

    public ManagedChannel createChannel(HostAndPort endpoint, boolean tls, boolean tlsDisableVerification, Map<String, String> metadataHash) {
        ManagedChannelBuilder managedChannelBuilder = createChannelBuilder(endpoint, tls, tlsDisableVerification, metadataHash);
        return managedChannelBuilder.build();
    }

    private ManagedChannelBuilder createChannelBuilder(HostAndPort endpoint, boolean tls, boolean tlsDisableVerification, Map<String, String> metadataHash) {
        if (tls) {
            try {
                io.netty.handler.ssl.SslContextBuilder grpcSslContexts = GrpcSslContexts.forClient();
                if(tlsDisableVerification){
                    grpcSslContexts.trustManager(InsecureTrustManagerFactory.INSTANCE);
                }

                return NettyChannelBuilder.forAddress(endpoint.getHost(), endpoint.getPort())
                        .negotiationType(NegotiationType.TLS)
                        .sslContext(grpcSslContexts
                            //force HTTP2 w/ ALPN support
                            .applicationProtocolConfig(
                                new ApplicationProtocolConfig(Protocol.ALPN, SelectorFailureBehavior.NO_ADVERTISE,
                                SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2))
                            .build())
                        .intercept(metadataInterceptor(metadataHash));
            } catch (SSLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
