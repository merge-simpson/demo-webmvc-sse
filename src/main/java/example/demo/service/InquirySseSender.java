package example.demo.service;

import example.common.sse.emitter.BaseSseEmitter;
import example.demo.dto.InquirySseMessage;
import example.common.utils.ExceptionUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

@Slf4j
public final class InquirySseSender {

    private final InquirySseMessage messageObject;

    private InquirySseSender(@NonNull InquirySseMessage messageObject) {
        this.messageObject = messageObject;
    }

    public static InquirySseSender with(InquirySseMessage messageObject) {
        return new InquirySseSender(messageObject);
    }

    public int sendTo(SseEmitter emitter) {
        try {
            SseEventBuilder event = SseEmitter.event().id("sse-id")
                            .name("sse") // client event name. ex: eventSource.addEventListner("sse", ...)
                            .reconnectTime(60000L)
                            .data(messageObject);

            emitter.send(event);
            return 1;
        } catch (IOException | IllegalStateException e) {
            String message = ExceptionUtil.stackTraceToString(e);
            log.error(STR."만료된 emitter: \{emitter}");
            if (log.isEnabledForLevel(Level.DEBUG)) {
                log.debug(STR."""
                        (stack trace) emitter 예외 정보

                        Emitter: \{emitter}
                        stack trace
                        \{message}""");
            }
        }

        return 0;
    }

    public void sendTo(Iterable<? extends BaseSseEmitter> emitters) {
        AtomicInteger count = new AtomicInteger();

        // foreach 내부에서 emitters의 원소가 삭제되면 forEach 길이가 변경되어 예외 발생
        //  java.util.ConcurrentModificationException: null  => solved
        // 일반 HashSet 등 사용 시.
        StreamSupport.stream(emitters.spliterator(), true)
                .parallel()
                .forEach((emitter) -> {
                    count.addAndGet(this.sendTo(emitter));
                });

        log.info("Inquiry SSE 전송 완료\n 수신 대상 수: {}\n message: {}", count.get(), messageObject);
    }
}