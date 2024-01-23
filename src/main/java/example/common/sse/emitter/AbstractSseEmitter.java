package example.common.sse.emitter;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;

public abstract class AbstractSseEmitter<KEY>
        extends SseEmitter
        implements Comparable<AbstractSseEmitter<KEY>> {
    protected KEY id;
    protected Instant createdAt;

    /**
     * 관리 호환성을 위해 만든 AbstractSseEmitter 클래스 생성자
     * @param id 여러 관리 방식의 호환성을 위해 아이디를 필수에 준하게 관리할 필요가 있음.
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
