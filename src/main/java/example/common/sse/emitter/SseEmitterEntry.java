package example.common.sse.emitter;

import lombok.Builder;
import lombok.NonNull;

import java.util.Comparator;
import java.util.Objects;

@Builder
public record SseEmitterEntry(
        @NonNull BaseSseEmitter receiver,
        String eventId,
        String eventName,
        Long reconnectTime,
        @NonNull Object message
) implements Comparable<SseEmitterEntry> {

    public SseEmitterEntry {
        if (eventId == null || eventId.isBlank()) {
            eventId = "sse-id";
        }
        if (eventName == null || eventName.isBlank()) {
            eventName = "sse";
        }
        if (reconnectTime == null) {
            reconnectTime = 60_000L;
        }

        eventId = eventId.strip();
        eventName = eventName.strip();
    }

    public static SseEmitterEntry of(BaseSseEmitter receiver, Object message) {
        return of(receiver, "sse-id", "sse", 60_0000L, message);
    }

    public static SseEmitterEntry of(BaseSseEmitter receiver, String eventId, String eventName, Object message) {
        return of(receiver, eventId, eventName, 60_000L, message);
    }

    public static SseEmitterEntry of(
            BaseSseEmitter receiver, String eventId, String eventName, Long reconnectTime, Object message) {
        return SseEmitterEntry.builder()
                .receiver(receiver)
                .eventId(eventId)
                .reconnectTime(reconnectTime)
                .eventName(eventName)
                .message(message)
                .build();
    }

    @Override
    public int compareTo(SseEmitterEntry o) {
        int result = this.receiver.compareTo(o.receiver());
        if (result == 0) {
            result = Objects.compare(
                    this.message,
                    o.message,
                    Comparator.comparingInt(Object::hashCode) // Concurrent 계열
            );
        }

        return result;
    }
}
