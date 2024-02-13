package example.tagged.exception;

import example.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum SubscriptionErrorCode implements ErrorCode {
    INVALID_TAG_ID("유효하지 않은 태그 아이디입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TAG_NAME("유효하지 않은 태그입니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;

    @Override
    public String defaultMessage() {
        return message;
    }

    @Override
    public HttpStatus defaultHttpStatus() {
        return status;
    }

    @Override
    public SubscriptionException defaultException() {
        return new SubscriptionException(this);
    }

    @Override
    public SubscriptionException defaultException(Throwable cause) {
        return new SubscriptionException(this, cause);
    }
}
