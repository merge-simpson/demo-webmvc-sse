package example.tags.service;

import example.tags.repository.TagQueryRepository;
import example.tags.usecase.TagPresenceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public final class TagQueryService implements TagPresenceUseCase {

    private final TagQueryRepository tagQueryRepository;

    @Override
    public boolean existsById(String id) {
        return tagQueryRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return tagQueryRepository.existsByName(name);
    }

    @Override
    public boolean existsAllByName(Collection<String> tagNames) {
        return tagNames.stream().allMatch(tagQueryRepository::existsByName);
    }
}
