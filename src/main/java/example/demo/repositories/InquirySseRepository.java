package example.demo.repositories;

import example.common.sse.emitter.BaseSseEmitter;
import example.common.sse.repository.SseRepository;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository("inquirySseRepository")
public class InquirySseRepository implements SseRepository<BaseSseEmitter, String> {

    private final Map<String, BaseSseEmitter> storage;

    public InquirySseRepository() {
        storage = new ConcurrentHashMap<>();
    }

    @Override
    public BaseSseEmitter save(@NonNull BaseSseEmitter emitter) {
        return storage.put(emitter.id(), emitter);
    }

    @Override
    public Collection<? extends BaseSseEmitter> findAll() {
        return storage.values();
    }

    @Override
    public void deleteById(@NonNull String id) {
        storage.remove(id);
    }
}
