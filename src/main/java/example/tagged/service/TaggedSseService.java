package example.tagged.service;

import example.common.sse.emitter.SseEmitterEventListeners;
import example.common.sse.emitter.TaggedSseEmitter;
import example.common.sse.repository.TaggedSseRepository;
import example.common.sse.tags.EmitterTag;
import example.tagged.usecase.TaggedSseSubscriptionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public final class TaggedSseService implements TaggedSseSubscriptionUseCase {

    private final TaggedSseRepository<TaggedSseEmitter, String> demoTaggedSseRepository;

    @Override
    public TaggedSseEmitter subscriptionWithTags(Collection<EmitterTag> tags) {
        String id = UUID.randomUUID().toString();
        var eventListeners = SseEmitterEventListeners.builder()
                .onComplete(() -> demoTaggedSseRepository.deleteById(id))
                .build();

        TaggedSseEmitter emitter = TaggedSseEmitter.builder()
                .id(id)
                .timeout(60_000L)
                .emitterEventListeners(eventListeners)
                .tags(tags)
                .build();

        return demoTaggedSseRepository.save(emitter);
    }

    @Override
    public TaggedSseEmitter subscriptionWithTags(EmitterTag... tags) {
        return subscriptionWithTags(Set.of(tags));
    }
}
