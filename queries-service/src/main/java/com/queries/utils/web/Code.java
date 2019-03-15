package com.queries.utils.web;


public interface Code {

    String SUCCESS_CODE = "200";
    String SUCCESS_DESC = "处理成功";

    String BAD_REQUEST_CODE = "400";
    String BAD_REQUEST_DESC = "参数错误";

    String SYSTEM_ERROR_CODE = "500";
    String SYSTEM_ERROR_DESC = "系统错误";
    String DATA_EXCEPTION_CODE = "600";
    String DATA_EXCEPTION_DESC = "数据异常";

    String BUSINESS_EXCEPTION = "700";

    String getCode();

    String getMessage();

}
