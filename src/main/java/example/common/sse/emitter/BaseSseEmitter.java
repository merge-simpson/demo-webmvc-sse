package example.common.sse.emitter;

import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public class BaseSseEmitter extends AbstractSseEmitter<BaseSseEmitter, String> {

    public BaseSseEmitter(@NonNull String id, Long timeout, SseEmitterEventListeners emitterEventListeners) {
        super(id, timeout);

        // Emitter 종료(보통 오류든 타임아웃이든 의도적 종료든 모두 이곳을 거침. 마치 finally 블록.)
        this.onCompletion(emitterEventListeners.onComplete());

        // Emitter 오류 시
        this.onError((e) -> {
            emitterEventListeners.onError().accept(e);
            if (emitterEventListeners.completeAfterTimeoutOrError()) {
                this.complete();
            }
        });

        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때)
        this.onTimeout(() -> {
            emitterEventListeners.onTimeout().run();
            if (emitterEventListeners.completeAfterTimeoutOrError()) {
                this.complete();
            }
        });
    }

    @Override
    public int compareTo(@NonNull BaseSseEmitter other) {
        return id.compareTo(other.id);
    }
}
