package example.demo.service;

import example.common.sse.emitter.BaseSseEmitter;
import example.common.utils.ExceptionUtil;
import example.demo.dto.InquirySseMessage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 *
 * @param to
 * @param message
 */
@Slf4j
public record InquiryNewSender(
        @NonNull Collection<? extends BaseSseEmitter> to,
        @NonNull InquirySseMessage message
) {
    public static InquiryNewSenderProxy prepare() {
        return new InquiryNewSenderProxy();
    }

    public static InquirySseSenderEachProxy prepareEach() {
        return new InquirySseSenderEachProxy();
    }

    public static InquirySseSenderEachProxy prepareEach(InquirySseEntry... entries) {
        return new InquirySseSenderEachProxy(entries);
    }

    public int send() {
        AtomicInteger count = new AtomicInteger();
        Set<BaseSseEmitter> receivers = new ConcurrentSkipListSet<>(to); // 중복 제거
        SseEventBuilder event = SseEmitter.event()
                .id("sse-id")
                .name("sse") // client event name. ex: eventSource.addEventListener("sse", ...)
                .reconnectTime(60_000L)
                .data(message);

        // foreach 내부에서 to의 원소가 삭제되면 예외 발생(java.util.ConcurrentModificationException: null)
        //  - 일반 HashMap, HashSet 등 사용 시 -> ConcurrentHashMap 등으로 전환 등
        //  - 지금은 repository 내에서 원본을 관리하고, 이곳에는 사본 사용 중. (solved)
        receivers
                .parallelStream()
                .forEach(consumerToSend(event, count));

        return count.get();
    }

    private Consumer<BaseSseEmitter> consumerToSend(SseEventBuilder event, AtomicInteger count) {
        return (emitter) -> {
            try {
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
        };
    }

    public static class InquiryNewSenderProxy {
        private final Set<BaseSseEmitter> to = new ConcurrentSkipListSet<>();
        private InquirySseMessage message;

        public InquiryNewSenderProxy to(Collection<? extends BaseSseEmitter> to) {
            this.to.addAll(to);
            return this;
        }

        @SafeVarargs
        public final <T extends BaseSseEmitter> InquiryNewSenderProxy add(T... to) {
            return to(Arrays.stream(to).toList());
        }

        public InquiryNewSenderProxy message(InquirySseMessage message) {
            this.message = message;
            return this;
        }

        public int send() {
            Objects.requireNonNull(message);
            InquiryNewSender sender = new InquiryNewSender(to, message);
            return sender.send();
        }
    }

    @NoArgsConstructor
    public static class InquirySseSenderEachProxy {
        private final Set<InquirySseEntry> entries = new ConcurrentSkipListSet<>();

        public InquirySseSenderEachProxy(InquirySseEntry... entries) {
            this.entries.addAll(Arrays.stream(entries).toList());
        }

        public InquirySseSenderEachProxy register(InquirySseEntry entry) {
            entries.add(entry);
            return this;
        }

        public int send() {
            AtomicInteger count = new AtomicInteger();
            entries.forEach((entry) -> {
                BaseSseEmitter emitter = entry.receiver();
                InquirySseMessage payload = entry.message();

                try {
                    SseEventBuilder event = SseEmitter.event()
                            .id("sse-id")
                            .name("sse") // client event name. ex: eventSource.addEventListener("sse", ...)
                            .reconnectTime(60_000L)
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

            return count.get();
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    public record InquirySseEntry(
            BaseSseEmitter receiver,
            InquirySseMessage message
    ) implements Comparable<InquirySseEntry> {
        public static InquirySseEntry of(BaseSseEmitter receiver, InquirySseMessage message) {
            return InquirySseEntry.builder()
                    .receiver(receiver)
                    .message(message)
                    .build();
        }

        @Override
        public int compareTo(InquirySseEntry o) {
            int result = this.receiver.compareTo(o.receiver());
            if (result == 0) {
                result = Objects.compare(
                        this.message,
                        o.message,
                        Comparator.comparingInt(Record::hashCode) // Concurrent 계열
                );
            }

            return result;
        }
    }
}
