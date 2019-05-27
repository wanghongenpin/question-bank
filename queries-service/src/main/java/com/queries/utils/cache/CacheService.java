package com.queries.utils.cache;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author wanghongen
 * 2018/5/5
 */
@Service
public class CacheService {
    private static Map<String, Object> cacheMap = new ConcurrentHashMap<>();

    public <T> T set(String key, T value) {
        return (T) cacheMap.put(key, value);
    }

    public <T> T setIfAbsent(String key, T value) {
        return (T) cacheMap.putIfAbsent(key, value);
    }

    public <T> T get(String key) {
        return (T) cacheMap.get(key);
    }

    public <T> T get(String key, Supplier<T> supplier) {
        return (T) cacheMap.computeIfAbsent(key, k -> supplier.get());
    }

    public <T> T remove(String key) {
        return (T) cacheMap.remove(key);
    }


}
