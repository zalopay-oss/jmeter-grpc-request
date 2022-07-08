package vn.zalopay.benchmark.core.channel;

import com.google.common.collect.ImmutableList;

import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ComponentObserver<T> implements StreamObserver<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentObserver.class);

    private final ImmutableList<StreamObserver<T>> observers;

    @SafeVarargs
    public static <T> ComponentObserver<T> of(StreamObserver<T>... observers) {
        return new ComponentObserver<>(ImmutableList.copyOf(observers));
    }

    private ComponentObserver(ImmutableList<StreamObserver<T>> observers) {
        this.observers = observers;
    }

    @Override
    public void onCompleted() {
        observers.forEach(StreamObserver::onCompleted);
    }

    @Override
    public void onError(Throwable t) {
        observers.forEach(
                tStreamObserver -> {
                    tStreamObserver.onError(t);
                });
    }

    @Override
    public void onNext(T value) {
        observers.forEach(
                tStreamObserver -> {
                    tStreamObserver.onNext(value);
                });
    }
}
