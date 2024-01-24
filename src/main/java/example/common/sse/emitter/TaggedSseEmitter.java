package example.common.sse.emitter;

import example.common.sse.tags.EmitterTag;
import lombok.NonNull;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class TaggedSseEmitter extends BaseSseEmitter {

    private final Set<EmitterTag> tags;

    public TaggedSseEmitter(
            @NonNull String id,
            Long timeout,
            Collection<? extends EmitterTag> tags,
            @NonNull SseEmitterEventListeners emitterEventListeners
    ) {
        super(id, timeout, emitterEventListeners);

        this.tags = new ConcurrentSkipListSet<>(tags);
    }

    public Set<EmitterTag> tags() {
        return new ConcurrentSkipListSet<>(tags);
    }
}
