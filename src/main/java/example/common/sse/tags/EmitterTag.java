package example.common.sse.tags;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record EmitterTag(
        @NonNull String id,
        @NonNull String name
) implements Comparable<EmitterTag> {
    public EmitterTag {
        if (id.isBlank() || name.isBlank()) {
            throw new RuntimeException("");
        }

        id = id.strip();
        name = name.strip();
    }

    @Override
    public int compareTo(EmitterTag o) {
        int result = this.id().compareTo(o.id());
        return result == 0 ? this.name().compareTo(o.name()) : result;
    }
}
