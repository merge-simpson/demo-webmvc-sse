package example.demo.service;

import example.demo.dto.SseTestMessage;
import example.common.sse.properties.SseEventConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.time.Instant;

@Component
public class DefaultSseTestSender implements SseTestSender {
    @Override
    public void sendTestTo(SseEmitter emitter) {
        SseTestMessage payload = SseTestMessage.builder()
                .content("이것은 연결 확인용이여.")
                .time(Instant.now())
                .build();

        SseEventConfigurationProperties config = SseEventConfigurationProperties.builder()
                .id("sse")
                .eventName("sse-test") // eventSource.addEventListener("sse-test", () => {})
                .reconnectTime(60_000L)
                .payload(payload)
                .build();

        sendTestTo(emitter, config);
    }

    @Override
    public void sendTestTo(SseEmitter emitter, SseEventConfigurationProperties config) {
        try {
            SseEventBuilder event = SseEmitter.event().id(config.id())
                    .name(config.eventName()) // FE event name: ~.addEventListner("sse-test", ...)
                    .reconnectTime(config.reconnectTime())
                    .data(config.payload());

            emitter.send(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
