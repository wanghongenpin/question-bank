package com.queries.controllers;

import com.queries.services.CourseService;
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
@RequestMapping("/questions/courses")
public class CourseController {
    @Resource
    private CourseService courseService;

    @GetMapping("/names")
    public Result<List<String>> getAllCourseName() {
        return Result.successful(courseService.getAllCourseName());
    }
}
