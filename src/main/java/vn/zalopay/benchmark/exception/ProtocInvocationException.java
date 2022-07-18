package vn.zalopay.benchmark.exception;

public class ProtocInvocationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProtocInvocationException(String message) {
        super(message);
    }

    public ProtocInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
