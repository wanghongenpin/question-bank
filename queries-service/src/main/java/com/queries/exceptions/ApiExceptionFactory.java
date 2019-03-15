package com.queries.exceptions;

/**
 * @author wanghongen
 * 2018/5/24
 */
public interface ApiExceptionFactory {
    String prefix();

    default ApiException apply(String errorCode, String errorMsg) {
        return new ApiException(prefix() + errorCode, errorMsg);
    }
}
