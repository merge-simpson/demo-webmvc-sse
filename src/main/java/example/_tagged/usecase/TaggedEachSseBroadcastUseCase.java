package example._tagged.usecase;

import example.common.sse.emitter.TaggedSseEmitter;

import java.util.Collection;
import java.util.List;

public interface TaggedEachSseBroadcastUseCase {
    List<TaggedSseEmitter> broadcastEachTo(Collection<? extends CharSequence> tags);
    String broadcastEachAsync(Collection<? extends CharSequence> tags);
}
