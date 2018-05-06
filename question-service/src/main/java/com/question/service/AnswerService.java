package com.question.service;

import com.question.model.Answer;

import java.util.List;

/**
 * @author wanghongen
 * 2018/5/5
 */
public interface AnswerService {
    List<Answer> batchSaveAnswer(List<Answer> answers);
}
