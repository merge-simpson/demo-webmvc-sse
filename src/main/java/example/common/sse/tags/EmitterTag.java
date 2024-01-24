package example.common.sse.tags;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record EmitterTag(
        @NonNull String id,
        @NonNull String name
) {
    public EmitterTag {
        if (id.isBlank() || name.isBlank()) {
            throw new RuntimeException("");
        }

        id = id.strip();
        name = name.strip();
    }
}
