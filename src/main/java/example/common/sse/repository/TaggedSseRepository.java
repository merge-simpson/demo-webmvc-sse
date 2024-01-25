package example.common.sse.repository;

import example.common.sse.emitter.TaggedSseEmitter;

import java.util.Collection;
import java.util.List;

public interface TaggedSseRepository<T extends TaggedSseEmitter, ID> extends SseRepository<T, ID> {
    List<TaggedSseEmitter> findAllByTagId(String tagId);
    List<TaggedSseEmitter> findAllByTagName(String tagName);
    List<TaggedSseEmitter> findAllByTagIds(Collection<? extends String> tagIds);
    List<TaggedSseEmitter> findAllByTagNames(Collection<? extends String> tagNames);
}
