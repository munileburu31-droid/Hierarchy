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


// Code Review:

// Expired cache entries are not automatically removed and remain in memory even after becoming invalid.
// Impact: Memory consumption continues to grow and may eventually cause the application to crash.

// Cache cleanup only occurs when a key is accessed; expired entries that are never read remain in memory.
// Impact: Stale data accumulates over time and reduces overall cache effectiveness.

// The expiration mechanism relies on system clock time, which can change unexpectedly.
// Impact: Cache entries may expire sooner or later than intended, leading to inconsistent behavior.

// There is no restriction on the cache size, allowing it to grow indefinitely.
// Impact: Continuous additions can increase memory usage and potentially trigger OutOfMemory errors.

// The expiration validation and value retrieval are not fully atomic even with a concurrent map.
// Impact: In high-concurrency scenarios, outdated or inconsistent values might be returned.

// When an entry expires, multiple threads may recompute the value simultaneously.
// Impact: This can create unnecessary load on downstream services and increase latency.
```
