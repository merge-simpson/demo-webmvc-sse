package example.common.sse.exception;

public class SseSenderClosedException extends RuntimeException {

    public static final String DEFAULT_MESSAGE =
            "이미 종료된 sender입니다. 이미 send() 메서드를 호출한 sender일 수 있습니다. 그 외 종료 지시가 있었는지 확인해야 합니다.";

    public SseSenderClosedException() {
        super(DEFAULT_MESSAGE);
    }

    public SseSenderClosedException(String message) {
        super(message);
    }

    public SseSenderClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SseSenderClosedException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public SseSenderClosedException(
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace
    ) {
        super(DEFAULT_MESSAGE, cause, enableSuppression, writableStackTrace);
    }

    public SseSenderClosedException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
