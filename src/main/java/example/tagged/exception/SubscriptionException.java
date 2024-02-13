package example.tagged.exception;

import example.common.exception.CustomException;
import example.common.exception.ErrorCode;

public class SubscriptionException extends CustomException {
    public SubscriptionException() {
        super();
    }

    public SubscriptionException(String message) {
        super(message);
    }

    public SubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubscriptionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SubscriptionException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
