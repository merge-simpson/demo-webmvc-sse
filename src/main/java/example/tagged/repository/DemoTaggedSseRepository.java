package example.tagged.repository;

import example.common.sse.emitter.TaggedSseEmitter;
import example.common.sse.repository.SseRepository;
import example.common.sse.tags.EmitterTag;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class DemoTaggedSseRepository implements SseRepository<TaggedSseEmitter, String> {

    private final Map<String, EmitterTag> tagsById = new ConcurrentHashMap<>();
    private final Map<String, EmitterTag> tagsByName = new ConcurrentHashMap<>();
    private final Map<String, TaggedSseEmitter> allEmitters = new ConcurrentHashMap<>();
    private final Map<EmitterTag, Set<TaggedSseEmitter>> inverseMapByTags
            = new ConcurrentHashMap<>();

    @Override
    public <S extends TaggedSseEmitter> S save(S emitter) {
        allEmitters.put(emitter.id(),  emitter);
        emitter.tags()
                .forEach((tag) -> {
                    Set<TaggedSseEmitter> set = getOrInitTagSet(tag);
                    set.add(emitter);
                });
        return null;
    }

    @Override
    public List<TaggedSseEmitter> findAll() {
        return allEmitters.values().stream().toList();
    }

    @Override
    public void deleteById(String id) {
        TaggedSseEmitter emitter = allEmitters.get(id);
        allEmitters.remove(id);
        emitter.tags()
                .forEach((tag) -> inverseMapByTags.get(tag).remove(emitter));
    }

    public List<TaggedSseEmitter> findAllByTagId(String tagId) {
        EmitterTag tag = tagsById.get(tagId);
        return inverseMapByTags.get(tag).stream().toList();
    }

    public List<TaggedSseEmitter> findAllByTagName(String tagName) {
        EmitterTag tag = tagsByName.get(tagName);
        return inverseMapByTags.get(tag).stream().toList();

    }

    public List<TaggedSseEmitter> findAllByTagIds(Collection<? extends String> tagIds) { // without duplication
        Set<TaggedSseEmitter> set = new HashSet<>();
        tagIds.forEach((tagId) -> {
            EmitterTag tag = tagsById.get(tagId);
            set.addAll(
                    inverseMapByTags.get(tag)
            );
        });

        return set.stream().toList();
    }

    public List<TaggedSseEmitter> findAllByTagNames(Collection<? extends String> tagNames) { // without duplication
        Set<TaggedSseEmitter> set = new HashSet<>();
        tagNames.forEach((tagId) -> {
            EmitterTag tag = tagsByName.get(tagId);
            set.addAll(
                    inverseMapByTags.get(tag)
            );
        });

        return set.stream().toList();
    }

    private Set<TaggedSseEmitter> getOrInitTagSet(EmitterTag tag) {
        Set<TaggedSseEmitter> set = inverseMapByTags.get(tag);

        if (set == null) {
            set = initTagSet(tag);
        }

        return set;
    }

    private synchronized Set<TaggedSseEmitter> initTagSet(EmitterTag tag) {
        Set<TaggedSseEmitter> set = inverseMapByTags.get(tag);

        if (set == null) {
            set = new ConcurrentSkipListSet<>();
            inverseMapByTags.put(tag, set);
            tagsById.put(tag.id(), tag);
            tagsByName.put(tag.name(), tag);
        }

        return set;
    }
}
