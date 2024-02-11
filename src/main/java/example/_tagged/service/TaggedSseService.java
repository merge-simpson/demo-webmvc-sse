package example._tagged.service;

import example.common.sse.emitter.SseEmitterEventListeners;
import example.common.sse.emitter.TaggedSseEmitter;
import example.common.sse.repository.TaggedSseRepository;
import example.common.sse.sender.GpSseSender;
import example.common.sse.tags.EmitterTag;
import example._tagged.usecase.TaggedEachSseBroadcastUseCase;
import example._tagged.usecase.TaggedEnBlocSseBroadcastUseCase;
import example._tagged.usecase.TaggedSseSubscriptionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public final class TaggedSseService
        implements TaggedSseSubscriptionUseCase,
        TaggedEnBlocSseBroadcastUseCase,
        TaggedEachSseBroadcastUseCase
{

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

    @Override
    public List<TaggedSseEmitter> broadcastEnBlocByTagNames(Collection<? extends CharSequence> tagNames) {
        List<TaggedSseEmitter> emitters = demoTaggedSseRepository.findAllByTagNames(tagNames);
        GpSseSender.prepareEnBloc("sse-id", "sse", 60_000L, "Hello")
                .appendAll(emitters)
                .send();

        return emitters;
    }

    @Override
    public String broadcastEnBlocAsync(Collection<? extends CharSequence> tags) {
        return null;
    }

    @Override
    public List<TaggedSseEmitter> broadcastEachTo(Collection<? extends CharSequence> tags) {
        return null;
    }

    @Override
    public String broadcastEachAsync(Collection<? extends CharSequence> tags) {
        return null;
    }
}
