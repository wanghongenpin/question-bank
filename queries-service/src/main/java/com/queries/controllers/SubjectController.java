package com.queries.controllers;

import com.queries.services.SubjectService;
import com.queries.utils.web.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wanghongen
 * 2018/5/6
 */
@RestController
@RequestMapping("/questions/subjects")
public class SubjectController {
    @Resource
    private SubjectService subjectService;

    @GetMapping("/names")
    public Result<List<String>> getAllSubjectName() {
        return Result.success(subjectService.getALlSubjectName());
    }
}
