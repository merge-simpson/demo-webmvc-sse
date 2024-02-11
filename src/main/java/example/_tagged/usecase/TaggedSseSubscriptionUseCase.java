package example._tagged.usecase;

import example.common.sse.emitter.TaggedSseEmitter;
import example.common.sse.tags.EmitterTag;

import java.util.Collection;

public interface TaggedSseSubscriptionUseCase {
    TaggedSseEmitter subscriptionWithTags(Collection<EmitterTag> tags);
    TaggedSseEmitter subscriptionWithTags(EmitterTag... tags);
}
