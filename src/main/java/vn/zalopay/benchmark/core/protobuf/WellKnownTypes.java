package vn.zalopay.benchmark.core.protobuf;

import com.google.common.collect.ImmutableSet;

/** Central place to store information about the protobuf well-known-types. */
public class WellKnownTypes {

    private static final ImmutableSet<String> FILES =
            ImmutableSet.of(
                    "any.proto",
                    "api.proto",
                    "descriptor.proto",
                    "duration.proto",
                    "empty.proto",
                    "field_mask.proto",
                    "source_context.proto",
                    "struct.proto",
                    "timestamp.proto",
                    "type.proto",
                    "wrappers.proto");

    public static ImmutableSet<String> fileNames() {
        return FILES;
    }
}
