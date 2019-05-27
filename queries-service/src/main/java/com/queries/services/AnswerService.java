package com.queries.services;

import com.queries.models.Answer;

import java.util.List;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/5
 */
public interface AnswerService {
    /**
     * 保存回答
     */
    Optional<Answer> getAnswer(String id);
    /**
     * 保存回答
     */
    Answer saveAnswer(Answer answer);

    /**
     * 批量保存回答
     *
     * @param answers List
     */
    List<Answer> batchSaveAnswer(List<Answer> answers);

    /**
     * 根据问题ID查询Answer
     *
     * @param questionId 问题ID
     */
    List<Answer> findAnswerByQuestionId(String questionId);
}
