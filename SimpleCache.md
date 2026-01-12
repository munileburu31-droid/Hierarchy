## Code Review

You are reviewing the following code submitted as part of a task to implement an item cache in a highly concurrent application. The anticipated load includes: thousands of reads per second, hundreds of writes per second, tens of concurrent threads.
Your objective is to identify and explain the issues in the implementation that must be addressed before deploying the code to production. Please provide a clear explanation of each issue and its potential impact on production behaviour.

```java
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMs = 60000; // 1 minute

    public static class CacheEntry<V> {
        private final V value;
        private final long timestamp;

        public CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public V getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null) {
            if (System.currentTimeMillis() - entry.getTimestamp() < ttlMs) {
                return entry.getValue();
            }
        }
        return null;
    }

    public int size() {
        return cache.size();
    }
}


//Code Review:
//Expired items are never removed from the cache; they remain in memory even after they are no longer valid.
//        Impact: Memory usage keeps increasing and can eventually crash the application.
//
// Cache cleanup happens only when a key is read; unused expired entries are never cleaned.
//Impact: Old data stays in memory and reduces cache efficiency over time.
//
// The expiration logic uses system clock time, which can change unexpectedly.
//Impact: Cache entries may expire too early or too late, causing unpredictable behavior.
//
// There is no limit on the cache size, so entries can grow without control.
//        Impact: Continuous writes can lead to high memory usage and OutOfMemory errors.
//
// The expiration check and value access are not fully atomic despite using a concurrent map.
//        Impact: Under heavy concurrency, stale or inconsistent data may be returned.
//
// Multiple threads may recompute the same value when an entry expires at the same time.
//        Impact: This can overload downstream systems and cause latency spikes.
```
