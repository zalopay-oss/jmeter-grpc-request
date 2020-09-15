package vn.zalopay.benchmark.core.io;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.TypeRegistry;
import java.io.BufferedReader;
import java.util.Objects;

public class MessageReader {
    private final JsonFormat.Parser jsonParser;
    private final Descriptor descriptor;
    private final BufferedReader bufferedReader;
    private final String source;
    private final String jsonData;

    public static MessageReader forJSON(Descriptor descriptor, TypeRegistry registry, String jsonData) {
        return new MessageReader(
            JsonFormat.parser().usingTypeRegistry(registry),
            descriptor,
            jsonData
        );
    }

    @VisibleForTesting
    MessageReader(
            JsonFormat.Parser jsonParser,
            Descriptor descriptor,
            BufferedReader bufferedReader,
            String source,
            String jsonData) {
        this.jsonParser = jsonParser;
        this.descriptor = descriptor;
        this.bufferedReader = bufferedReader;
        this.source = source;
        this.jsonData = jsonData;
    }

    @VisibleForTesting
    MessageReader(
            JsonFormat.Parser jsonParser,
            Descriptor descriptor,
            String jsonData) {
        this.jsonParser = jsonParser;
        this.descriptor = descriptor;
        this.bufferedReader = null;
        this.source = "Using json data";
        this.jsonData = jsonData;
    }

    public ImmutableList<DynamicMessage> readWithFile() {
        if (Objects.isNull(bufferedReader))
            throw new IllegalArgumentException("BufferedReader is null because using json data");

        ImmutableList.Builder<DynamicMessage> resultBuilder = ImmutableList.builder();
        try {
            String line;
            boolean wasLastLineEmpty = false;
            while (true) {
                line = bufferedReader.readLine();

                // Two consecutive empty lines mark the end of the stream.
                if (Strings.isNullOrEmpty(line)) {
                    if (wasLastLineEmpty) {
                        return resultBuilder.build();
                    }
                    wasLastLineEmpty = true;
                    continue;
                } else {
                    wasLastLineEmpty = false;
                }

                // Read the next full message.
                StringBuilder stringBuilder = new StringBuilder();
                while (!Strings.isNullOrEmpty(line)) {
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
                wasLastLineEmpty = true;

                DynamicMessage.Builder nextMessage = DynamicMessage.newBuilder(descriptor);
                jsonParser.merge(stringBuilder.toString(), nextMessage);

                // Clean up and prepare for next message.
                resultBuilder.add(nextMessage.build());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to read messages from: " + source, e);
        }
    }

    public ImmutableList<DynamicMessage> readWithJsonData() {
        ImmutableList.Builder<DynamicMessage> resultBuilder = ImmutableList.builder();
        try {
            DynamicMessage.Builder nextMessage = DynamicMessage.newBuilder(descriptor);
            jsonParser.merge(jsonData, nextMessage);

            // Clean up and prepare for next message.
            resultBuilder.add(nextMessage.build());
            return resultBuilder.build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to read messages from: " + jsonData, e);
        }
    }

    public ImmutableList<DynamicMessage> read() {
        if (!Strings.isNullOrEmpty(jsonData))
            return readWithJsonData();
        return readWithFile();
    }
}
