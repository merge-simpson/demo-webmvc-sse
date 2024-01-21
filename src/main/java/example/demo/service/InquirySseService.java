package example.demo.service;

import example.common.sse.emitter.BaseSseEmitter;
import example.common.sse.emitter.SseEmitterEventListeners;
import example.common.sse.repository.SseRepository;
import example.demo.dto.InquirySseMessage;
import example.demo.usecase.InquiryBroadcastUseCase;
import example.demo.usecase.InquirySubscribeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public final class InquirySseService implements InquirySubscribeUseCase, InquiryBroadcastUseCase {

    private final SseTestSender testSender;
    private final SseRepository<BaseSseEmitter, String> sseRepository;

    @Override
    public SseEmitter subscribe() {
        String clientId = UUID.randomUUID().toString();
        SseEmitterEventListeners eventListeners = this.defaultEventListeners(clientId);
        BaseSseEmitter emitter = new BaseSseEmitter(clientId, 3_600_000L, eventListeners);

        // SSE 구독 시 처음에 sendTest라도 해서 아무 데이터를 보내는 이유:
        // (타임아웃이나 complete 된 상태에서 set 등에 살아 있던 sse emitter에게 데이터 보내려 하면 오류)
        this.sendTest(emitter);

        sseRepository.save(emitter); // <<< 우리도 Emitter를 보존, Emitter가 클라이언트를 기억(하는 것과 같은 효과).

        return emitter; // 구독 요청 때 SseEmitter를 반환하면 스프링이 Emitter마다 클라이언트를 기억하는 것으로 보임. (Response)
    }

    @Override
    public void broadcast(InquirySseMessage messageObject) {
        InquirySseSender sender = InquirySseSender.with(messageObject);
        sender.sendTo(sseRepository.findAll());
    }

    private void sendTest(SseEmitter emitter) {
        log.info("Connected Emitter(during sending for test): " + emitter);

        // send:
        testSender.sendTestTo(emitter);
    }

    private SseEmitterEventListeners defaultEventListeners(String id) {
        return SseEmitterEventListeners.builder()
                .onComplete(() -> sseRepository.deleteById(id))
                .completeAfterTimeoutOrError(true)
                .build();
    }
}
