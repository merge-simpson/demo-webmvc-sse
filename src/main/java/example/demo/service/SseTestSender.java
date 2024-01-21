package example.demo.service;

import example.common.sse.properties.SseEventConfigurationProperties;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseTestSender {

    /**
     * 첫 연결 시 반드시 테스트 전송해 줄 것.
     */
    void sendTestTo(SseEmitter emitter);
    void sendTestTo(SseEmitter emitter, SseEventConfigurationProperties config);
}
