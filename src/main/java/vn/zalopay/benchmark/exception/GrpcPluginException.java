package vn.zalopay.benchmark.exception;

public class GrpcPluginException extends RuntimeException {
    private static final long serialVersionUID = 0L;

    public GrpcPluginException(String message) {
        super(message);
    }

    public GrpcPluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
