package com.queries.utils.web;

import lombok.Getter;

@Getter
public enum ResultCode implements Code {

    OK(SUCCESS_CODE, SUCCESS_DESC),

    DATA_EXCEPTION(DATA_EXCEPTION_CODE, DATA_EXCEPTION_DESC),
    SYSTEM_ERROR(SYSTEM_ERROR_CODE, SYSTEM_ERROR_DESC),

    USER_NOT_EXIST("1001", "用户不存在"),
    USERNAME_OR_PASSWORD_ERROR("1002", "用户名或密码错误"),
    SERVER_BUSY("10000", "服务器正忙，请稍后..."),
    FORBID("10001", "禁止接口调用");

    private String code;
    private String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
