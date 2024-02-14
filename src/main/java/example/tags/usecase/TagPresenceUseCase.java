package example.tags.usecase;

import java.util.Collection;

public interface TagPresenceUseCase {
    boolean existsById(String id);
    boolean existsByName(String name);
    boolean existsAllByName(Collection<String> tagNames);
}
