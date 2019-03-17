package com.queries.exceptions;

import com.common.utils.alarm.EmailAlarm;
import com.queries.utils.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.io.IOException;

import static java.util.stream.Collectors.joining;

/**
 * 异常处理类
 *
 * @author wanghongen
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @Resource
    private EmailAlarm emailAlarm;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Result<?> bindExceptionHandler(MethodArgumentNotValidException ex) {
        //获取校验失败的字段的错误信息
        final String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(joining("\n"));
        log.warn("无效请求参数 message={}", message);
        return Result.badRequestError(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Result<String> bindExceptionHandler(ConstraintViolationException ex) {
        //获取校验失败的字段的错误信息
        final String message = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(joining("\n"));
        log.warn("无效请求参数 message={}", message);
        return Result.badRequestError(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Result<String> bindExceptionHandler(IllegalArgumentException ex) {
        //获取校验失败的字段的错误信息
        final String message = ex.getMessage();
        log.warn("无效请求参数 message={}", message, ex);
        return Result.badRequestError(message);
    }

    /**
     * 包装异常信息返回
     *
     * @param exception Exception
     * @return ResponseResult<Object>
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleApiException(Throwable exception, HttpServletRequest request) {
        if (exception instanceof ApiException) {
            ApiException e = (ApiException) exception;

            return new Result<>(e);
        }
        final String url = request.getMethod() + ' ' + request.getRequestURI() + (request.getQueryString() == null ? "" : '?' + request.getQueryString());
        //忽略HEAD请求
        if (!request.getMethod().equalsIgnoreCase("HEAD") && !(exception instanceof IOException && "Connection reset by peer".equalsIgnoreCase(exception.getMessage()))) {
            emailAlarm.alarm("请求服务异常 " + url, exception);
            exception.printStackTrace();
        }
        log.error("异常处理器 url:{},message={},exception:{}", url, exception.getMessage(), exception);
        return Result.fail(exception);
    }

}
