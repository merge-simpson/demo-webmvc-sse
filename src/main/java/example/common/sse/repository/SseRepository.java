package example.common.sse.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;

public interface SseRepository<T extends SseEmitter, KEY> {
    <S extends T> S save(S emitter);
    Collection<? extends T> findAll();
    void deleteById(KEY id);
}
