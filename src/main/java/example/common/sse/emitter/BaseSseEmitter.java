package example.common.sse.emitter;

import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public class BaseSseEmitter extends AbstractSseEmitter<String> {

    public BaseSseEmitter(
            @NonNull String id,
            Long timeout,
            @NonNull SseEmitterEventListeners emitterEventListeners
    ) {
        super(id, timeout);

        // Emitter 종료 (종료 시 재사용 불가)
        this.onCompletion(emitterEventListeners.onComplete());

        // Emitter 오류 시
        this.onError((e) -> {
            emitterEventListeners.onError().accept(e);

            boolean willComplete = emitterEventListeners.completeAfterTimeoutOrError();
            if (willComplete) {
                this.complete();
            }
        });

        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때)
        this.onTimeout(() -> {
            emitterEventListeners.onTimeout().run();

            boolean willComplete = emitterEventListeners.completeAfterTimeoutOrError();
            if (willComplete) {
                this.complete();
            }
        });
    }

    @Override
    public int compareTo(@NonNull AbstractSseEmitter<String> other) {
        return id.compareTo(other.id());
    }
}
