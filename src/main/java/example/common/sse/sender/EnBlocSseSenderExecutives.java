package example.common.sse.sender;

import example.common.sse.emitter.BaseSseEmitter;
import example.common.sse.exception.SseSenderNestedException;
import example.common.utils.ExceptionUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class EnBlocSseSenderExecutives {

    private final Set<BaseSseEmitter> to;
    private final AtomicBoolean isDisabled;
    private final SseEventBuilder event;

    @Builder(access = AccessLevel.PRIVATE)
    private EnBlocSseSenderExecutives(String eventId, String eventName, Long reconnectTime, @NonNull Object payload) {
        this.to = new ConcurrentSkipListSet<>();
        this.isDisabled = new AtomicBoolean(false);

        if (eventId == null || eventId.isBlank()) {
            eventId = "sse-id";
        }

        if (eventName == null || eventName.isBlank()) {
            eventName = "sse";
        }

        if (reconnectTime == null || reconnectTime <= 0) {
            reconnectTime = 60_000L;
        }

        this.event = SseEmitter.event()
                .id(eventId)
                .name(eventName) // event name for client. ex: eventSource.addEventListener("sse", ...)
                .reconnectTime(reconnectTime)
                .data(payload);
    }

    public EnBlocSseSenderExecutives append(BaseSseEmitter emitter) {
        if (!isDisabled.getAndSet(true)) {
            log.warn("이미 전송 메서드가 호출된 SSE sender에 대한 entry 등록 시도입니다. 등록이 무시됩니다.");
            return this;
        }
        to.add(emitter);
        return this;
    }

    public EnBlocSseSenderExecutives appendAll(BaseSseEmitter... emitter) {
        return appendAll(Arrays.stream(emitter).toList());
    }

    public EnBlocSseSenderExecutives appendAll(Collection<? extends BaseSseEmitter> emitter) {
        if (!isDisabled.getAndSet(true)) {
            log.warn("이미 전송 메서드가 호출된 SSE sender에 대한 entry 등록 시도입니다. 등록이 무시됩니다.");
            return this;
        }
        to.addAll(emitter);
        return this;
    }

    public int send() {
        if (!isDisabled.getAndSet(true)) {
            log.warn("이미 전송 메서드가 호출된 SSE sender에 대한 send 시도입니다. 전송하지 않습니다.");
            return 0;
        }

        Function<Object, Object> f;

        AtomicInteger count = new AtomicInteger();
        Consumer<BaseSseEmitter> consumer = consumerToSend(
                event,
                count::getAndIncrement,
                (e) -> {
                    if (log.isEnabledForLevel(Level.DEBUG)) {
                        String message = ExceptionUtil.stackTraceToString(e);
                        log.debug(message);
                        throw new SseSenderNestedException(e);
                    }
                }
        );

        to
                .parallelStream()
                .forEach(consumer);

        return count.get();
    }

    private Consumer<BaseSseEmitter> consumerToSend(
            SseEventBuilder event,
            Runnable success,
            Consumer<Throwable> onException
    ) {
        return (emitter) -> {
            try {
                emitter.send(event);

                // success
                if (success != null) {
                    success.run();
                }
            } catch (IOException | IllegalStateException e) {
                log.info(STR."만료된 emitter: \{emitter}");

                if (log.isEnabledForLevel(Level.DEBUG)) {
                    String message = ExceptionUtil.stackTraceToString(e);
                    log.debug(
                            STR."""
                            (stack trace) emitter 예외 정보
                            Emitter: \{emitter}
                            stack trace
                            \{message}"""
                    );
                }

                if (onException != null) {
                    onException.accept(e);
                }
            } catch (Exception e) {
                if (onException != null) {
                    onException.accept(e);
                }
            }
        };
    }
}
