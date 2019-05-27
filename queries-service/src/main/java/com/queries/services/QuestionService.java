package com.queries.services;

import com.queries.models.Question;
import com.queries.request.QuestionQuery;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/4
 */
public interface QuestionService {
    /**
     * 获取试题
     *
     * @param id ID
     */
    Optional<Question> getQuestion(String id);

    /**
     * 获取试题
     *
     * @param id ID
     */
    Optional<Question> getQuestion(String id, String question);

    /**
     * 列表查询
     *
     * @param query 搜索条件
     */
    Page<Question> list(QuestionQuery query);

    /**
     * 获取所有问题类型
     */
    List<String> getAllQuestionType();

    /**
     * 保存试题
     *
     * @param question 试题
     */
    Question saveQuestion(Question question);

    /**
     * 批量保存试题
     *
     * @param questions 试题集合
     */
    List<Question> batchSaveQuestion(Iterable<Question> questions);
}
