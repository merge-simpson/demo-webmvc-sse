package example.common.async.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class AtomicCounterRepository {

    private final CounterMapHolder holder;

    public AtomicCounterRepository() {
        super();
        holder = new CounterMapHolder();
    }

    public int incrementAndGet(CharSequence key) {
        AtomicInteger counter = holder.getCounter(key);
        return counter.incrementAndGet();
    }

    public int get(CharSequence key) {
        return holder.getCounter(key).get();
    }

    public int getAndDelete(CharSequence key) {
        return holder.deleteCounter(key).get();
    }

    private static final class CounterMapHolder {
        private final Map<CharSequence, AtomicInteger> counterMap;

        CounterMapHolder() {
            counterMap = new ConcurrentHashMap<>();
        }

        AtomicInteger getCounter(CharSequence key) {
            AtomicInteger counter = counterMap.get(key);
            return counter != null ? counter : create(key);
        }

        AtomicInteger deleteCounter(CharSequence key) {
            return counterMap.remove(key);
        }

        private AtomicInteger create(CharSequence key) {
            synchronized (counterMap) {
                if (counterMap.get(key) != null) {
                    return counterMap.get(key);
                }
                return counterMap.put(key, new AtomicInteger(0));
            }
        }
    }
}
