package com.queries.utils.cache;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author wanghongen
 * 2018/5/7
 */
@AllArgsConstructor
@Data
public class Cache<T> {
    private T value;
    private TimeUnit expirationTime;
    private long lastUpdateTime;

}
