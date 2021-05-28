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
        observers.forEach(tStreamObserver -> {
            try {
                tStreamObserver.onCompleted();
            } catch (Exception t) {
                LOGGER.warn(t.getMessage());
            }
        });
    }

    @Override
    public void onError(Throwable t) {
        observers.forEach(tStreamObserver -> {
            try {
                tStreamObserver.onError(t);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
            }
        });
    }

    @Override
    public void onNext(T value) {
        observers.forEach(tStreamObserver -> {
            try {
                tStreamObserver.onNext(value);
            } catch (Exception t) {
                LOGGER.warn(t.getMessage());
            }
        });
    }
}