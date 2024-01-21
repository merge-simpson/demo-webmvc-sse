package example.demo.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record InquirySseMessage(
        String content,
        Instant time
) {
}
