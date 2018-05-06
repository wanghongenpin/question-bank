package com.question.utils.cache;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanghongen
 * 2018/5/5
 */
@Service
public class CacheService {
    private static Map<String, Object> cacheMap = new ConcurrentHashMap<>();
    private static ThreadLocal<Object> threadLocal = new ThreadLocal<>();

    public <T> T set(String key, T value) {
        return (T) cacheMap.put(key, value);
    }

    public <T> void set(T value) {
        threadLocal.set(value);
    }

    public <T> T get() {
        return (T) threadLocal.get();
    }

    public <T> T get(String key) {
        return (T) cacheMap.get(key);
    }

    public <T> T remove(String key) {
        return (T) cacheMap.remove(key);
    }


}
