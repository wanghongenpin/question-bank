package com.question.utils.web;


public interface Code {

    int SUCCESS_CODE = 200;
    String SUCCESS_DESC = "处理成功";

    int SYSTEM_ERROR_CODE = 500;
    String SYSTEM_ERROR_DESC = "系统错误";
    int DATA_EXCEPTION_CODE = 600;
    String DATA_EXCEPTION_DESC = "数据异常";

    int BUSINESS_EXCEPTION = 700;

    int getCode();

    String getMessage();

}
