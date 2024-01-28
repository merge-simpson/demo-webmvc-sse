package example.common.sse.sender;

import example.common.sse.emitter.BaseSseEmitter;
import example.common.sse.emitter.SseEmitterEntry;
import example.common.utils.ExceptionUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@NoArgsConstructor
public class EachSseSenderExecutives {
    private final Set<SseEmitterEntry> entries = new ConcurrentSkipListSet<>();
    private final AtomicBoolean isDisabled = new AtomicBoolean(false);

    public EachSseSenderExecutives(SseEmitterEntry... entries) {
        this.entries.addAll(Arrays.stream(entries).toList());
    }

    public EachSseSenderExecutives append(SseEmitterEntry entry) {
        if (isDisabled.get()) {
            log.warn("이미 전송 메서드가 호출된 SSE sender에 대한 entry 등록 시도입니다. 등록이 무시됩니다.");
            return this;
        }
        entries.add(entry);
        return this;
    }

    public EachSseSenderExecutives append(BaseSseEmitter emitter, Object payload) {
        return append(SseEmitterEntry.of(emitter, payload));
    }

    public int send() {
        if (isDisabled.getAndSet(true)) {
            log.warn("이미 전송 메서드가 호출된 SSE sender에 대한 send 시도입니다. 전송하지 않습니다.");
            return 0;
        }

        AtomicInteger count = new AtomicInteger();
        entries.forEach((entry) -> {
            BaseSseEmitter emitter = entry.receiver();
            Object payload = entry.message();

            try {
                SseEventBuilder event = SseEmitter.event()
                        .id(entry.eventId())
                        .name(entry.eventName()) // client event name. ex: eventSource.addEventListener("sse", ...)
                        .reconnectTime(entry.reconnectTime())
                        .data(payload);

                emitter.send(event);
                count.getAndIncrement();
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
            }
        });

        entries.clear();
        return count.get();
    }
}