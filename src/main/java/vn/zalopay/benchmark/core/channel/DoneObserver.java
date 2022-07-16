package vn.zalopay.benchmark.core.channel;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DoneObserver<T> implements StreamObserver<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoneObserver.class);
    private final SettableFuture<Void> doneFuture;

    public DoneObserver() {
        this.doneFuture = SettableFuture.create();
    }

    @Override
    public synchronized void onCompleted() {
        doneFuture.set(null);
    }

    @Override
    public synchronized void onError(Throwable t) {
        doneFuture.setException(t);
    }

    @Override
    public void onNext(T next) {
        LOGGER.debug("On next gRPC message: {}", next);
    }

    /**
     * Returns a future which completes when the rpc finishes. The returned future fails if the rpc
     * fails.
     */
    public ListenableFuture<Void> getCompletionFuture() {
        return doneFuture;
    }
}
