package vn.zalopay.benchmark.core.specification;

import java.util.ArrayList;
import java.util.List;

public class GrpcResponse {

    private boolean success;
    private Throwable throwable;
    private final List<Object> output;

    public GrpcResponse() {
        output = new ArrayList<>();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void storeGrpcMessage(Object message) {
        output.add(message);
    }

    public String getGrpcMessageString() {
        if (output.size() == 1) {
            return output.get(0).toString();
        }

        return output.toString();
    }
}
