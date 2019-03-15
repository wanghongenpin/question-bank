package com.queries.controllers;

import com.queries.services.UserService;
import com.queries.utils.cache.CacheService;
import com.queries.utils.web.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wanghongen
 * 2019-03-15
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Resource
    private CacheService cacheService;
    @Resource
    private UserService userService;

    @PostMapping("/upload")
    public Result uploadUserQuestionBank(String username, String password) {
        if (cacheService.get(username) != null) {
            return Result.success("已经在爬取题库,请尝试搜索");
        }
        return userService.uploadUserQuestionBank(username, password).fold(Result::fail, right -> Result.success("登录成功,正在爬取题库"));
    }
}
