package example.common.sse.emitter;

import example.common.utils.ExceptionUtil;
import example.common.utils.ThreadUtil;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import java.util.function.Consumer;

/**
 *
 * @param onComplete () -> {}
 * @param onError (Throwable e) -> {}
 * @param onTimeout () -> {}
 * @param completeAfterTimeoutOrError true to complete after error or timeout, and default is true.
 */
@Builder
@Slf4j
public record SseEmitterEventListeners(
        @NonNull Runnable onComplete,
        Consumer<Throwable> onError,
        Runnable onTimeout,
        Boolean completeAfterTimeoutOrError
) {
    public SseEmitterEventListeners {
        if (completeAfterTimeoutOrError == null) {
            completeAfterTimeoutOrError = true;
        }

        if (onError == null) {
            onError = (e) -> {
                if (log != null && log.isEnabledForLevel(Level.DEBUG)) {
                    String stackTrace = ExceptionUtil.stackTraceToString(e);
                    log.debug("SSE Emitter 오류\n{}", stackTrace);
                }
            };
        }

        if (onTimeout == null) {
            onTimeout = () -> {
                if (log != null && log.isEnabledForLevel(Level.DEBUG)) {
                    String callStack = ThreadUtil.callStack();
                    log.debug(STR."SSE Emitter 타임아웃\n\{callStack}");
                }
            };
        }
    }
}