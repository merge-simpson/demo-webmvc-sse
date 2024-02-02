package example.tagged.repository;

import example.tagged.repository.types.CompletableFutureWrapper;
import example.tagged.repository.types.FutureStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class AsyncSseFutureRepository {
    private final Map<CharSequence, CompletableFutureWrapper<?>> futureMap = new ConcurrentHashMap<>();

    public <T> CompletableFutureWrapper<T> save(CharSequence id, CompletableFuture<T> future) {
        CompletableFutureWrapper<T> savingFuture = new CompletableFutureWrapper<>(id.toString(), future, Instant.now());

        futureMap.put(id, savingFuture);

        return savingFuture;
    }

    public <T> Optional<CompletableFutureWrapper<T>> findWrapperById(CharSequence id, Class<T> clazz) {
        CompletableFutureWrapper<?> wildcardWrapper = futureMap.get(id);

        if (wildcardWrapper == null) {
            return Optional.empty();
        }
        if (!clazz.isInstance(futureMap.get(id))) {
            throw new RuntimeException("");
        }

        // NOTE ensured casting: (CompletableFutureWrapper<T>) wildcardWrapper
        CompletableFutureWrapper<T> wrapper = (CompletableFutureWrapper<T>) wildcardWrapper;
        return Optional.of(wrapper);
    }

    public <T> Optional<CompletableFuture<T>> findFutureById(CharSequence id, Class<T> clazz) {
        return findWrapperById(id, clazz).map(CompletableFutureWrapper::value);
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

        CompletableFutureWrapper<?> future = futureMap.get(id);
        return future.isDone();
    }

    public boolean existsById(CharSequence id) {
        return futureMap.containsKey(id);
    }

    public FutureStatus statusById(CharSequence id) {
        if (!existsById(id)) {
            return FutureStatus.NOT_FOUND;
        }
        CompletableFutureWrapper<?> future = futureMap.get(id);

        int bit = 0;
        bit = future.isDone() ? 1 : 0;
        bit |= future.isCancelled() ? 1 << 1 : 0;
        bit |= future.isCompletedExceptionally() ? 1 << 2 : 0;

        return switch (bit) {
            case 0b0000 -> FutureStatus.RUNNING;
            case 0b0001 -> FutureStatus.DONE;
            case 0b0010 -> FutureStatus.CANCELED;
            case 0b0100 -> FutureStatus.EXCEPTION;
            default -> throw new Error("");
        };
    }

    /**
     *
     * @return completed future wrappers
     */
    public Map<CharSequence, CompletableFutureWrapper<?>> clearCompletedItems(Instant from, Instant to) {
        Map<CharSequence, CompletableFutureWrapper<?>> newMap = new HashMap<>();
        futureMap.keySet()
                .forEach((key) -> {
                    CompletableFutureWrapper<?> wrapper = futureMap.get(key);
                    if(wrapper.isBetween(from, to) && wrapper.isCompleted()) {
                        newMap.put(key, futureMap.remove(key));
                    }
                });
        return Collections.unmodifiableMap(newMap);
    }

    public Map<CharSequence, CompletableFutureWrapper<?>> clearCompletedItemsAfter(Instant from) {
        Map<CharSequence, CompletableFutureWrapper<?>> newMap = new HashMap<>();
        futureMap.keySet()
                .forEach((key) -> {
                    CompletableFutureWrapper<?> wrapper = futureMap.get(key);
                    boolean willRemove = wrapper.isBetween(from, Instant.MAX) && wrapper.isCompleted();

                    if (willRemove) {
                        newMap.put(key, futureMap.remove(key));
                    }
                });
        return Collections.unmodifiableMap(newMap);
    }

    public Map<CharSequence, CompletableFutureWrapper<?>> clearCompletedItemsBefore(Instant to) {
        Map<CharSequence, CompletableFutureWrapper<?>> newMap = new HashMap<>();
        futureMap.keySet()
                .forEach((key) -> {
                    CompletableFutureWrapper<?> wrapper = futureMap.get(key);
                    boolean willRemove = wrapper.isBetween(Instant.MIN, to) && wrapper.isCompleted();
                    if (willRemove) {
                        newMap.put(key, futureMap.remove(key));
                    }
                });
        return Collections.unmodifiableMap(newMap);
    }
}
