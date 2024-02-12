package example.tags.repository;

import example.tags.entity.SseTag;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository("tagQueryRepository")
public class TagQueryRepository {
    private final List<SseTag> tags; // instead of DB
    private final Map<String, SseTag> indexedById;

    public TagQueryRepository() {
        tags = List.of(
                new SseTag("a", "inquiry-count"),
                new SseTag("b", "manager-schedule"),
                new SseTag("c", "example3"),
                new SseTag("d", "example4")
        );
        indexedById = new ConcurrentHashMap<>();

        tags.forEach((tag) -> indexedById.put(tag.getId(), tag));
    }

    public List<SseTag> findAll() {
        return tags;
    }

    public Optional<SseTag> findById(String id) {
        return Optional.ofNullable(indexedById.get(id));
    }

    public Optional<SseTag> findByName(String name) {
        return tags.stream()
                .filter((tag) -> Objects.equals(name, tag.getName()))
                .findAny();
    }

    public boolean existsById(String id) {
        return indexedById.containsKey(id);
    }

    public boolean existsByName(String name) {
        return tags.stream().anyMatch((tag) -> Objects.equals(name, tag.getName()));
    }
}
