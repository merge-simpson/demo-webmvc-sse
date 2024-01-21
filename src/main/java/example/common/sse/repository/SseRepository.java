package example.common.sse.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseRepository<T extends SseEmitter, KEY> {
    <S extends T> S save(S emitter);
    Iterable<? extends T> findAll();
    void deleteById(KEY id);
}
