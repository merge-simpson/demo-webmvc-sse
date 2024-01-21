package example.demo.controller;

import example.demo.dto.InquirySseMessage;
import example.demo.usecase.InquiryBroadcastUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public final class TriggerApi {

    private final InquiryBroadcastUseCase inquiryBroadcastUseCase;

    @PostMapping("/trigger")
    public void onFirebaseDbChanged(/* ... */) {
        InquirySseMessage messageObject = InquirySseMessage.builder()
                .content("Example Data: " + UUID.randomUUID())
                .time(Instant.now())
                .build();

        inquiryBroadcastUseCase.broadcast(messageObject);
    }
}
