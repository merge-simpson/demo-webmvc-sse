package example.tagged.repository;

import example.common.sse.emitter.TaggedSseEmitter;
import example.common.sse.repository.TaggedSseRepository;
import example.common.sse.tags.EmitterTag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Repository("demoTaggedSseRepository")
@Slf4j
public class DemoTaggedSseRepository implements TaggedSseRepository<TaggedSseEmitter, String> {

    private final Map<String, EmitterTag> tagsById = new ConcurrentHashMap<>();
    private final Map<String, EmitterTag> tagsByName = new ConcurrentHashMap<>();
    private final Map<String, TaggedSseEmitter> allEmitters = new ConcurrentHashMap<>();
    private final Map<EmitterTag, Set<TaggedSseEmitter>> inverseMapByTags
            = new ConcurrentHashMap<>();

    @Override
    public TaggedSseEmitter save(TaggedSseEmitter emitter) {
        TaggedSseEmitter previousEmitter = allEmitters.put(emitter.id(), emitter);

        if (previousEmitter != null) {
            removeEmitterFromTagSet(previousEmitter);
        }

        addEmitterToTagSet(emitter);

        log.debug(
                STR."""
                Current Emitters Map(Saved):

                * All: \{allEmitters}
                * Tag ID Map: \{tagsById}
                * Tag Name Map: \{tagsByName}
                * Inverse Map: \{inverseMapByTags}
                """
        );

        return emitter;
    }

    @Override
    public List<TaggedSseEmitter> findAll() {
        return allEmitters.values().stream().toList();
    }

    @Override
    public void deleteById(String id) {
        TaggedSseEmitter emitter = allEmitters.get(id);
        allEmitters.remove(id);
        removeEmitterFromTagSet(emitter);
    }

    @Override
    public List<TaggedSseEmitter> findAllByTagId(String tagId) {
        EmitterTag tag = tagsById.get(tagId);
        return inverseMapByTags.get(tag).stream().toList();
    }

    @Override
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

    private Set<TaggedSseEmitter> getTagSetOrInit(EmitterTag tag) {
        return inverseMapByTags.computeIfAbsent(tag, (k) -> {
            var set = new ConcurrentSkipListSet<TaggedSseEmitter>();
            inverseMapByTags.put(tag, set);
            tagsById.put(tag.id(), tag);
            tagsByName.put(tag.name(), tag);
            return set;
        });
    }

    /**
     * @deprecated 이 동작은 ConcurrentHashMap에서 자동으로 관리됨.
     * @param emitter
     * @return returns previous emitter if the ID is already exists.
     * Or returns null, if there was no duplicated ID in the map.
     */
    @Deprecated(since = "", forRemoval = true)
    private TaggedSseEmitter saveEmitterToMainStorageSync(TaggedSseEmitter emitter) {
        synchronized (allEmitters) {
            return allEmitters.put(emitter.id(),  emitter);
        }
    }

    /**
     *
     * @param emitter
     */
    private void addEmitterToTagSet(TaggedSseEmitter emitter) {
        synchronized (emitter) {
            emitter
                    .tags()
                    .forEach((tag) -> getTagSetOrInit(tag).add(emitter));
        }
    }

    /**
     *
     * @param emitter
     */
    private synchronized void removeEmitterFromTagSet(TaggedSseEmitter emitter) {
        emitter
                .tags()
                .forEach((tag) -> {
                    Set<TaggedSseEmitter> set = getTagSetOrInit(tag);
                    set.remove(emitter);
                });
    }
}
