package com.queries.controllers;

import com.queries.events.AutomaticOnlineTestPaperEvent;
import com.queries.events.UploadQueriesEvent;
import com.queries.exceptions.QueriesServiceException;
import com.queries.services.UserService;
import com.queries.utils.cache.CacheService;
import com.queries.utils.web.Result;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author wanghongen
 * 2019-03-15
 */
@RestController
@RequestMapping("/questions/users")
public class UserController {
    @Resource
    private CacheService cacheService;
    @Resource
    private UserService userService;
    @Resource
    private ApplicationContext applicationContext;

    @PostMapping("/upload")
    public Result uploadUserQuestionBank(String username, String password) {
        if (cacheService.get(username) != null) {
            return Result.successful("已经在爬取题库,请尝试搜索");
        }
        return userService.login(username, password).fold(Result::fail, right -> {
                    try {
                        applicationContext.publishEvent(new UploadQueriesEvent(right));
                    } catch (RejectedExecutionException e) {
                        //线程池拒绝
                        return Result.fail(QueriesServiceException.RejectedExecutionException.build());
                    }
                    return Result.successful("登录成功,正在爬取题库");
                }
        );
    }

    /**
     * 在线测试
     */
    @PostMapping("/online_test_paper")
    public Result automaticOnlineTestPaper(String username, String password) {
        return userService.login(username, password).fold(Result::fail, right -> {
                    try {
                        applicationContext.publishEvent(new AutomaticOnlineTestPaperEvent(right));
                    } catch (RejectedExecutionException e) {
                        //线程池拒绝
                        return Result.fail(QueriesServiceException.RejectedExecutionException.build());
                    }
                    return Result.successful("登录成功,正在自动答题");
                }
        );
    }

}
