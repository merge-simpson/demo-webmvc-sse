package example.common.utils;

import example.common.exception.ErrorCode;

public final class Preconditions {
    public static void validate(boolean condition, ErrorCode errorCode) {
        if (!condition) {
            throw errorCode.defaultException();
        }
    }
}
