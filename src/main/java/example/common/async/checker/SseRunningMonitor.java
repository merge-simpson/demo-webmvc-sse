package example.common.async.checker;

import lombok.Builder;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @deprecated
 */
@Deprecated
public final class SseRunningMonitor {
    private final int total;
    private final AtomicInteger successCounter;
    private final AtomicInteger cancelledCounter;
    private final AtomicInteger exceptionCounter;
    private final InstantHolder lastUpdatedAt;

    private boolean isCompleted = false;

    public SseRunningMonitor(int total) {
        this(total, new AtomicInteger(), new AtomicInteger(), new AtomicInteger(), new InstantHolder());
    }

    public SseRunningMonitor(
            int total,
            AtomicInteger successCounter,
            AtomicInteger cancelledCounter,
            AtomicInteger exceptionCounter,
            InstantHolder lastUpdatedAt
    ) {
        this.total = total;
        this.successCounter = successCounter;
        this.cancelledCounter = cancelledCounter;
        this.exceptionCounter = exceptionCounter;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public SseRunningCapture capture() {
        return SseRunningCapture.builder()
                .total(total)
                .success(successCounter.get())
                .cancelled(cancelledCounter.get())
                .exception(cancelledCounter.get())
                .build();
    }

    public boolean increaseSuccess() {
        return increaseTo(successCounter);
    }

    public boolean increaseCancelled() {
        return increaseTo(cancelledCounter);
    }

    public boolean increaseException() {
        return increaseTo(exceptionCounter);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public double completionRate() {
        double completion = successCounter.get() + cancelledCounter.get();
        return completion / total;
    }

    private boolean increaseTo(AtomicInteger target) {
        if (isCompleted) {
            throw new RuntimeException("");
        }

        target.incrementAndGet();
        lastUpdatedAt.updateInstant();
        return updateStatus();
    }

    private boolean updateStatus() {
        int completion = successCounter.get()
                + cancelledCounter.get()
                + exceptionCounter.get();

        return isCompleted = completion >= total;
    }

    @Builder
    public record SseRunningCapture(
            int total,
            int success,
            int cancelled,
            int exception
    ) {
        public double completionRate() {
            return (double) (success + exception) / total;
        }
    }

    public static final class InstantHolder {
        private Instant instant;

        public InstantHolder() {
            instant = Instant.now();
        }

        public InstantHolder(Instant instant) {
            this.instant = instant;
        }

        public void updateInstant() {
            instant = Instant.now();
        }

        public void updateInstant(Instant instant) {
            this.instant = instant;
        }
    }
}
