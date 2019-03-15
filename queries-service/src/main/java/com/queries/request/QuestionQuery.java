package com.queries.request;

import lombok.Data;

/**
 * @author wanghongen
 * 2018/5/6
 */
@Data
public class QuestionQuery {
    private int page = 1;
    private int size = 10;
    private String title;
    private String subject;
    private String questionType;
    private String answer;
}
