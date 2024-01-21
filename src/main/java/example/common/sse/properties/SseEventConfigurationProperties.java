package example.common.sse.properties;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record SseEventConfigurationProperties(
        @NonNull String id,
        @NonNull String eventName,
        Long reconnectTime,
        Object payload
) {
    public SseEventConfigurationProperties {
        if (reconnectTime == null) {
            reconnectTime = 60_000L;
        }
    }
}
