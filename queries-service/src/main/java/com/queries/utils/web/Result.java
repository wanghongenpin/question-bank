package com.queries.utils.web;

import com.queries.exceptions.ApiException;
import lombok.Data;

@Data
public class Result<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public Result() {
    }

    private Result(T data) {
        this.success = true;
        this.code = Code.SUCCESS_CODE;
        this.message = Code.SUCCESS_DESC;
        this.data = data;
    }

    public Result(ApiException e) {
        this.success = false;
        this.code = e.getErrorCode();
        this.message = e.getErrorMsg();
    }

    public Result(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> Result<T> badRequestError(String message) {
        return new Result<>(false, ResultCode.BAD_REQUEST_CODE, message, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }


    public static Result<String> result(ResultCode resultCode) {
        Result<String> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setSuccess(ResultCode.OK.getCode().equals(resultCode.getCode()));
        result.setData(resultCode.getMessage());
        return result;
    }

    /**
     * 返回异常
     *
     * @param e Exception
     */
    public static <T> Result<T> fail(Throwable e) {
        if (e instanceof ApiException) {
            return new Result<>((ApiException) e);
        }

        return new Result<>(false, ResultCode.SYSTEM_ERROR_CODE, e.getMessage(), null);
    }
}