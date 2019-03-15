package com.queries.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ApiException
 *
 * @author wanghongen
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = -6609651618576467104L;
    private String errorCode;

    private String errorMsg;

    public ApiException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

}
