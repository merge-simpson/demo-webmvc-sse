package example.demo.service;

import example.common.sse.emitter.BaseSseEmitter;
import example.common.utils.ExceptionUtil;
import example.demo.dto.InquirySseMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

@Slf4j
public record InquiryNewSender(
        @NonNull Iterable<? extends BaseSseEmitter> to,
        @NonNull InquirySseMessage message
) {
    public static InquiryNewSenderProxy prepare() {
        return new InquiryNewSenderProxy();
    }

    public int send() {
        SseEventBuilder event = SseEmitter.event().id("sse-id")
                .name("sse") // client event name. ex: eventSource.addEventListener("sse", ...)
                .reconnectTime(60000L)
                .data(message);

        AtomicInteger count = new AtomicInteger();

        // foreach 내부에서 to의 원소가 삭제되면 forEach 길이가 변경되어 예외 발생
        //  java.util.ConcurrentModificationException: null  => solved
        //  - 일반 HashMap, HashSet 등 사용 시 -> ConcurrentHashMap 등으로 변환
        //  - 지금은 repository 내에서 원본을 관리하고, 이곳에는 사본 사용 중. (solved)
        StreamSupport.stream(to.spliterator(), true)
                .parallel()
                .forEach(consumerToSend(event, count));

        return count.get();
    }

    private Consumer<BaseSseEmitter> consumerToSend(SseEventBuilder event, AtomicInteger count) {
        return (emitter) -> {
            try {
                emitter.send(event);
                count.getAndIncrement();
            } catch (IOException | IllegalStateException e) {
                String message = ExceptionUtil.stackTraceToString(e);

                log.info(STR."만료된 emitter: \{emitter}");

                if (log.isEnabledForLevel(Level.DEBUG)) {
                    log.debug(STR."""
                                    (stack trace) emitter 예외 정보
                                    Emitter: \{emitter}
                                    stack trace
                                    \{message}"""
                    );
                }
            }
        };
    }

    public static class InquiryNewSenderProxy {
        private Iterable<? extends BaseSseEmitter> to;
        private InquirySseMessage message;

        public InquiryNewSenderProxy to(Iterable<? extends BaseSseEmitter> to) {
            this.to = to;
            return this;
        }

        public InquiryNewSenderProxy message(InquirySseMessage message) {
            this.message = message;
            return this;
        }

        public int send() {
            InquiryNewSender sender = new InquiryNewSender(to, message);
            return sender.send();
        }
    }
}
