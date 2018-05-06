package com.question.queries;

import lombok.Data;

/**
 * @author wanghongen
 * 2018/5/6
 */
@Data
public class QuestionQueries {
    private int page = 1;
    private int size = 10;
    private String title;
    private String subject;
    private String questionType;
}
