package vn.zalopay.benchmark.core.message;


import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import vn.zalopay.benchmark.exception.GrpcPluginException;

public class Reader {
    private final JsonFormat.Parser jsonParser;
    private final Descriptors.Descriptor descriptor;
    private String payload;

    Reader(JsonFormat.Parser jsonParser, Descriptors.Descriptor descriptor, String payload) {
        this.jsonParser = jsonParser;
        this.descriptor = descriptor;
        this.payload = payload;
    }

    public static Reader create(Descriptors.Descriptor descriptor, String payloadData,
                                JsonFormat.TypeRegistry registry) {
        return new Reader(JsonFormat.parser().usingTypeRegistry(registry).ignoringUnknownFields(), descriptor, payloadData);
    }

    public ImmutableList<DynamicMessage> read() {
        ImmutableList.Builder<DynamicMessage> resultBuilder = ImmutableList.builder();
        try {
            DynamicMessage.Builder nextMessage = DynamicMessage.newBuilder(descriptor);
            jsonParser.merge(payload, nextMessage);
            // Clean up and prepare for next message.
            resultBuilder.add(nextMessage.build());
            return resultBuilder.build();
        } catch (Exception e) {
            throw new GrpcPluginException("Unable to read messages from: " + payload, e);
        }
    }
}
