package vn.zalopay.benchmark.core.specification;

import java.util.ArrayList;
import java.util.List;

public class GrpcResponse {
    private final List<Object> output;

    public GrpcResponse() {
        output = new ArrayList<>();
    }

    public void storeGrpcMessage(Object message) {
        output.add(message);
    }

    public String getGrpcMessageString() {
        if (output.size() == 1)
            return output.get(0).toString();
        return output.toString();
    }

}
