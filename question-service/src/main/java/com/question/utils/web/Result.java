package com.question.utils.web;

import lombok.Data;

@Data
public class Result<T> {
    private boolean success;
    private int code;
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

    public static <T> Result<T> of(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }


    public static Result<String> result(ResultCode resultCode) {
        Result<String> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setSuccess(ResultCode.OK.getCode() == resultCode.getCode());
        result.setData(resultCode.getMessage());
        return result;
    }
}