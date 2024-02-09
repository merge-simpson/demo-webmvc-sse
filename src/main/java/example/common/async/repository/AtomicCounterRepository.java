package example.common.async.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class AtomicCounterRepository {

    private final CounterMapProxy delegator;

    public AtomicCounterRepository() {
        super();
        delegator = new CounterMapProxy();
    }

    public int incrementAndGet(CharSequence key) {
        AtomicInteger counter = delegator.getCounter(key);
        return counter.incrementAndGet();
    }

    public int getById(CharSequence id) {
        return delegator.getCounter(id).get();
    }

    public int getAndDelete(CharSequence key) {
        return delegator.deleteCounter(key).get();
    }

    private static final class CounterMapProxy {
        private final Map<CharSequence, AtomicInteger> counterMap;

        CounterMapProxy() {
            counterMap = new ConcurrentHashMap<>();
        }

        AtomicInteger getCounter(CharSequence key) {
            AtomicInteger counter = counterMap.get(key);
            return counter != null ? counter : create(key);
        }

        AtomicInteger deleteCounter(CharSequence key) {
            return counterMap.remove(key);
        }

        void clear(CharSequence key) {
            counterMap.get(key).set(0);
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
