package example.common.sse.sender.types;

import lombok.Builder;
import lombok.NonNull;

import java.time.Instant;

@Builder
public record SseEmitterDispatchResult(
        @NonNull String emitterId,
        @NonNull SseCompletionStatus status,
        Instant completedAt
) {
    public SseEmitterDispatchResult {
        if (completedAt == null) {
            completedAt = Instant.now();
        }
    }
}
