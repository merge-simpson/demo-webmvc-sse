package example.common.sse.sender;

import example.common.sse.emitter.SseEmitterEntry;

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

    private GpSseSender() {}
}
