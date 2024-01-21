package example.demo.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record SseTestMessage(
        String content,
        Instant time
) {
}
