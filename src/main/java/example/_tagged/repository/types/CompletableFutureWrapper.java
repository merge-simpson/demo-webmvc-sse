package example._tagged.repository.types;

import lombok.Builder;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public final class CompletableFutureWrapper<T> {
    private final String id;
    private final CompletableFuture<T> future;
    private final Instant createdAt;

    public CompletableFutureWrapper(String id, CompletableFuture<T> future) {
        this(id, future, Instant.now());
    }

    @Builder
    public CompletableFutureWrapper(String id, CompletableFuture<T> future, Instant createdAt) {
        this.id = id;
        this.future = future;
        this.createdAt = createdAt;
    }

    public String id() {
        return id;
    }

    public CompletableFuture<T> value() {
        return future;
    }

    public boolean isDone() {
        return future.isDone();
    }

    public boolean isCancelled() {
        return future.isCancelled();
    }

    public boolean isCompletedExceptionally() {
        return future.isCompletedExceptionally();
    }

    public boolean isCompleted() {
        return isDone() || isCancelled() || isCompletedExceptionally();
    }

    public Instant createdAt() {
        return createdAt;
    }

    public boolean isBetween(Instant from, Instant to) {
        return createdAt.isAfter(from) && createdAt.isBefore(to);
    }
}
