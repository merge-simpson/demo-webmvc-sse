package example.demo.usecase;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface InquirySubscribeUseCase {
    SseEmitter subscribe();
}
