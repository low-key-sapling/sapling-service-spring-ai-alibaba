package com.sapling.framework.ai.alibaba.cache;

import com.sapling.framework.ai.alibaba.config.AlibabaAiProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Cache manager for AI responses.
 * 
 * <p>This is a simple in-memory cache implementation.
 * For production use with Redis, inject RedissonClient and use RMap.
 * 
 * @author Sapling Team
 * @since 1.0.0
 */
@Slf4j
public class AiCacheManager {

    private final AlibabaAiProperties.CacheProperties properties;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public AiCacheManager(AlibabaAiProperties.CacheProperties properties) {
        this.properties = properties;
    }

    /**
     * Get value from cache or load it
     */
    public <T> T get(String key, Supplier<T> loader) {
        if (!properties.getEnabled()) {
            return loader.get();
        }
        
        String fullKey = properties.getKeyPrefix() + key;
        
        CacheEntry entry = cache.get(fullKey);
        if (entry != null && !entry.isExpired()) {
            log.debug("Cache hit for key: {}", fullKey);
            @SuppressWarnings("unchecked")
            T value = (T) entry.getValue();
            return value;
        }
        
        log.debug("Cache miss for key: {}", fullKey);
        T value = loader.get();
        
        if (value != null) {
            put(fullKey, value);
        }
        
        return value;
    }

    /**
     * Put value into cache
     */
    public void put(String key, Object value) {
        if (!properties.getEnabled()) {
            return;
        }
        
        String fullKey = properties.getKeyPrefix() + key;
        
        // Check cache size limit
        if (cache.size() >= properties.getMaxSize()) {
            evictOldest();
        }
        
        long expiryTime = System.currentTimeMillis() + (properties.getTtl() * 1000L);
        cache.put(fullKey, new CacheEntry(value, expiryTime));
        
        log.debug("Cached value for key: {}", fullKey);
    }

    /**
     * Remove value from cache
     */
    public void evict(String key) {
        String fullKey = properties.getKeyPrefix() + key;
        cache.remove(fullKey);
        log.debug("Evicted cache for key: {}", fullKey);
    }

    /**
     * Clear all cache
     */
    public void clear() {
        cache.clear();
        log.info("Cleared all cache");
    }

    /**
     * Evict oldest entries when cache is full
     */
    private void evictOldest() {
        cache.entrySet().stream()
            .filter(entry -> entry.getValue().isExpired())
            .map(Map.Entry::getKey)
            .findFirst()
            .ifPresent(cache::remove);
    }

    /**
     * Cache entry with expiry time
     */
    private static class CacheEntry {
        private final Object value;
        private final long expiryTime;

        public CacheEntry(Object value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        public Object getValue() {
            return value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}
