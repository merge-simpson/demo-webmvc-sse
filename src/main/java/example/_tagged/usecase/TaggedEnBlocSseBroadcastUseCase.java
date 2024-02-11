package example._tagged.usecase;

import example.common.sse.emitter.TaggedSseEmitter;

import java.util.Collection;
import java.util.List;

public interface TaggedEnBlocSseBroadcastUseCase {
    List<TaggedSseEmitter> broadcastEnBlocByTagNames(Collection<? extends CharSequence> tags);
    String broadcastEnBlocAsync(Collection<? extends CharSequence> tags);
}
