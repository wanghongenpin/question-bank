package com.queries.parses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author wanghongen
 * 2019-05-19
 */
@AllArgsConstructor
@Data
public class LearningCourses {
    private String name;
    private List<TestPaper> testPaperList;
}
