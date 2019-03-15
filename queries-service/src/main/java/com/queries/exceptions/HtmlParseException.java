package com.queries.exceptions;

/**
 * @author wanghongen
 * 2019-03-14
 */
public class HtmlParseException extends ApiException {
    public HtmlParseException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
