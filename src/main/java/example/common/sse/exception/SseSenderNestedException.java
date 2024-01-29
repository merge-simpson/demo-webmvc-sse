package example.common.sse.exception;

public class SseSenderNestedException extends RuntimeException {
    public SseSenderNestedException() {
    }

    public SseSenderNestedException(Throwable e) {
        super(e);
    }
}
