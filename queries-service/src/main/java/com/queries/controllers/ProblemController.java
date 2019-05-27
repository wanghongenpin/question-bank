package com.queries.controllers;

import com.queries.request.ProblemSubmit;
import com.queries.services.ProblemService;
import com.queries.utils.web.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author wanghongen
 * 2019-05-27
 */
@RestController
@RequestMapping("/questions/problems")
public class ProblemController {
    @Resource
    private ProblemService problemService;

    @PostMapping("/submit")
    public Result submit(@Valid @RequestBody ProblemSubmit problemSubmit) {
        problemService.submit(problemSubmit);
        return Result.successful(true);
    }
}
