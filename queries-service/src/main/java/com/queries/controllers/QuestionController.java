package com.queries.controllers;

import com.queries.models.Question;
import com.queries.request.QuestionQuery;
import com.queries.services.QuestionService;
import com.queries.utils.web.Result;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wanghongen
 * 2018/5/5
 */
@RestController
@RequestMapping("/questions")
public class QuestionController {
    @Resource
    private QuestionService questionService;

    @GetMapping
    public Result<Page<Question>> list(QuestionQuery queries) {
        Page<Question> list = questionService.list(queries);
        return Result.success(list);
    }

    @GetMapping("/types")
    public Result<List<String>> getAllQuestionType() {
        return Result.success(questionService.getAllQuestionType());
    }
}
