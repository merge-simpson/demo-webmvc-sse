package example.common.sse.emitter;

import lombok.experimental.SuperBuilder;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;

public abstract class AbstractSseEmitter<KEY>
        extends SseEmitter
        implements Comparable<AbstractSseEmitter<KEY>> {
    protected KEY id;
    protected Instant createdAt;

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
