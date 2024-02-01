package example.tagged.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Repository
@Slf4j
public class AsyncSseFutureRepository {
    private final Map<CharSequence, CompletableFuture<?>> futureMap = new ConcurrentHashMap<>();

    public <T> Optional<CompletableFuture<T>> findAndDeleteIfCompleted(CharSequence id, Class<T> clazz) {
        CompletableFuture<?> future = futureMap.get(id);
        if (future == null) {
            return Optional.empty();
        }

        try {
            if (!clazz.isInstance(future.get())) {
                throw new RuntimeException("");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        boolean isCompleted = future.isDone();
        isCompleted |= future.isCancelled();
        isCompleted |= future.isCompletedExceptionally();

        if (isCompleted) {
            futureMap.remove(id);
        }

        return Optional.of((CompletableFuture<T>) future); // NOTE !clazz.isInstance(item) assures the casting.
    }

    public <T> CompletableFuture<T> save(CharSequence id, CompletableFuture<T> future) {
        return (CompletableFuture<T>) futureMap.put(id, future);
    }

    public void deleteById(CharSequence id) {
        if (!existsById(id)) {
            log.warn(STR."Attempted to delete absent SSE future. ID: \{id}");
            return;
        }

        futureMap.remove(id);
    }

    public boolean isDoneById(CharSequence id) {
        if (!existsById(id)) {
            throw new RuntimeException("");
        }

        CompletableFuture<?> future = futureMap.get(id);
        return future.isDone();
    }

    public boolean existsById(CharSequence id) {
        CompletableFuture<?> future = futureMap.get(id);
        return future != null;
    }

    public Map<CharSequence, CompletableFuture<?>> clearCompletedItems() {
        Map<CharSequence, CompletableFuture<?>> newMap = new ConcurrentHashMap<>();
        futureMap.keySet()
                .forEach((key) -> {
                    CompletableFuture<?> future = futureMap.get(key);
                    boolean isCompleted = future.isDone();

                    isCompleted |= future.isCancelled();
                    isCompleted |= future.isCompletedExceptionally();

                    if(isCompleted) {
                        newMap.put(key, futureMap.remove(key));
                    }
                });

        return Collections.unmodifiableMap(newMap);
    }
}
