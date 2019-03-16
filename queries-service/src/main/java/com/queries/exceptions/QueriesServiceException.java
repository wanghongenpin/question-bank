package com.queries.exceptions;

import com.queries.utils.web.ResultCode;

/**
 * @author wanghongen
 * 2018/5/24
 */
public enum QueriesServiceException implements ApiExceptionFactory {
    /* badRequestExceptionCode = "4000" */
    /*  internalServerExceptionCode = "5000"*/
    /* internalApiExceptionCode = "6000"*/

    IllegalUserException(ResultCode.USERNAME_OR_PASSWORD_ERROR.getCode(), ResultCode.USERNAME_OR_PASSWORD_ERROR.getMessage()),
    IllegalTokenException("4001", "无效令牌,请勿登陆郑州大学网站"),

    RejectedExecutionException("4002", "当前爬取任务过多,请稍后上传题库"),
    ;

    @Override
    public String prefix() {
        return "QB";
    }

    private String errorCode;

    private String errorMsg;

    QueriesServiceException(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ApiException build() {
        return apply(errorCode, errorMsg);
    }

}
