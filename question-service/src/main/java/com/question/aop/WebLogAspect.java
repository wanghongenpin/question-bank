package com.question.aop;


import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wanghongen
 * 2018/5/1
 */
@Aspect
@Component
public class WebLogAspect {
    private final Logger accessLogger = LoggerFactory.getLogger("access");

    private final Logger requestLogger = LoggerFactory.getLogger("request");

    private final List<String> methods =
            Arrays.asList(HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.PATCH.name());
    private final ThreadLocal<Long> startTimeLocal = new ThreadLocal<>();

    private static final Long SLOW_TIME = 1000L;

    private static final AtomicLong accessCount = new AtomicLong(0);

    /**
     * 切入点
     */
    @Pointcut("execution(* com.question.controller..*.*(..))")
    public void webLog() {
    }

    /**
     * 在方法执行之前执行
     *
     * @param joinPoint JoinPoint
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        Long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String remoteAddr = request.getRemoteAddr();

        if (methods.contains(method)) {
            String uri = request.getRequestURI();
            Object[] args = joinPoint.getArgs();
            requestLogger
                    .info("Method: {}, URI: {}, remoteAddr: {}, RequestBody: {}", method, uri, remoteAddr, JSON.toJSONString(args));
        }
        startTimeLocal.set(startTime);

    }

    /**
     * 在方法执行之后执行
     */
    @AfterReturning("webLog()")
    public void doAfterReturning() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        String method = request.getMethod();
        String query = request.getQueryString() == null ? "" : "?" + request.getQueryString();
        String uri = request.getRequestURI() + query;
        String remoteAddr = request.getRemoteAddr();
        assert response != null;
        int status = response.getStatus();
        Long endTime = System.currentTimeMillis();
        Long requestTime = endTime - startTimeLocal.get();
        String mark = requestTime > SLOW_TIME ? "SLOW" : "NORMAL";
        accessLogger
                .info("{} {} {} {} {} {} {}", method, uri, remoteAddr, accessCount.getAndIncrement(), status, requestTime, mark);
    }
}
