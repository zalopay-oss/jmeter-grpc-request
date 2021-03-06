package vn.zalopay.benchmark.core.message;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.zalopay.benchmark.core.specification.GrpcResponse;

public class Writer<T extends Message> implements StreamObserver<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Writer.class);

    private final JsonFormat.Printer jsonPrinter;
    private final GrpcResponse output;

    /**
     * Creates a new Writer which writes the messages it sees to the supplied
     * Output.
     */
    public static <T extends Message> Writer<T> create(GrpcResponse output, JsonFormat.TypeRegistry registry) {
        return new Writer<>(JsonFormat.printer().usingTypeRegistry(registry), output);
    }

    Writer(JsonFormat.Printer jsonPrinter, GrpcResponse output) {
        this.jsonPrinter = jsonPrinter.preservingProtoFieldNames().includingDefaultValueFields();
        this.output = output;
    }

    @Override
    public void onCompleted() {
        LOGGER.debug("On completed gRPC message: {}", output.getGrpcMessageString());
    }

    @Override
    public void onError(Throwable throwable) {
        String stacktrace = ExceptionUtils.getStackTrace(throwable);
        String throwMsg = throwable.getMessage();
        output.storeGrpcMessage(String.format("%s. %s", throwMsg, stacktrace));
        LOGGER.error(throwable.getMessage());
    }

    @Override
    public void onNext(T message) {
        try {
            output.storeGrpcMessage(jsonPrinter.print(message));
        } catch (InvalidProtocolBufferException e) {
            LOGGER.warn(e.getMessage());
        }
    }
}
