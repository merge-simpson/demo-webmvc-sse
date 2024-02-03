package example.common.sse.sender.types;

public enum SseCompletionStatus {
    DONE,
    CANCELLED,
    EXCEPTIONALLY

    // NOTE "RUNNING" 제외해도 될 것(완료된 상태만 사용해도 될 것)으로 봄.
}
