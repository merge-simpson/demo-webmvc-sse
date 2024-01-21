package example.common.sse.emitter;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;

public abstract class AbstractSseEmitter<SELF extends AbstractSseEmitter<SELF, KEY>, KEY>
        extends SseEmitter
        implements Comparable<SELF> {
    KEY id;
    Instant createdAt;

    /**
     *
     * @param id
     * @param timeout milliseconds
     */
    protected AbstractSseEmitter(KEY id, Long timeout) {
        super(timeout);
        this.id = id;
        createdAt = Instant.now();
    }

    public KEY id() {
        return id;
    }
}
