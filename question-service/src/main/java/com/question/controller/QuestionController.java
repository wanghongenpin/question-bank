package com.question.controller;

import com.question.model.Question;
import com.question.queries.QuestionQueries;
import com.question.service.QuestionService;
import com.question.utils.cache.CacheService;
import com.question.utils.web.Result;
import com.question.utils.web.ResultCode;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/5
 */
@RestController
@RequestMapping("/questions")
public class QuestionController {
    @Resource
    private QuestionService questionService;
    @Resource
    private CacheService cacheService;

    @PostMapping("/upload")
    public Result uploadUserQuestionBank(String username, String password) {
        if (cacheService.get(username) != null) {
            return Result.success("已经在爬取题库,请尝试搜索");
        }
        Optional<String> optional = questionService.uploadUserQuestionBank(username, password);
        if (optional.isPresent()) {
            return Result.success("登录成功,正在爬取题库");
        }
        return Result.result(ResultCode.USERNAME_OR_PASSWORD_ERROR);
    }

    @GetMapping
    public Result<Page<Question>> list(QuestionQueries queries) {
        Page<Question> list = questionService.list(queries);
        return Result.success(list);
    }

    @GetMapping("/types")
    public Result<List<String>> getAllQuestionType() {
        return Result.success(questionService.getAllQuestionType());
    }
}
