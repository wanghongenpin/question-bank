package com.queries.exceptions;

/**
 * @author wanghongen
 * 2019-03-13
 */
public class RestApiException extends ApiException {
    public RestApiException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
