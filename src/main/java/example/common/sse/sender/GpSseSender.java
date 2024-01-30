package example.common.sse.sender;

import example.common.sse.emitter.SseEmitterEntry;
import example.common.sse.sender.EnBlocSseSenderExecutives.EnBlocSseSenderExecutivesBuilder;

import java.util.Collection;

/**
 * General Purpose SSE Sender
 */
public class GpSseSender {
    public static EachSseSenderExecutives prepareEach() {
        return new EachSseSenderExecutives();
    }

    public static EachSseSenderExecutives prepareEach(SseEmitterEntry... entries) {
        return new EachSseSenderExecutives(entries);
    }

    public static EachSseSenderExecutives prepareEach(Collection<? extends SseEmitterEntry> emitterEntries) {
        SseEmitterEntry[] entries = new SseEmitterEntry[emitterEntries.size()];
        return new EachSseSenderExecutives(emitterEntries.toArray(entries));
    }

    public static EnBlocSseSenderExecutives prepareEnBloc(
            String eventId,
            String eventName,
            Long reconnectTime,
            Object payload
    ) {
        return EnBlocSseSenderExecutives.builder()
                .eventId(eventId)
                .eventName(eventName)
                .reconnectTime(reconnectTime)
                .payload(payload)
                .build();
    }

    private GpSseSender() {}
}
