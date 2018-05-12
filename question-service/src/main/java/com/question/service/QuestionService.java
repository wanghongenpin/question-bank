package com.question.service;

import com.question.model.Question;
import com.question.queries.QuestionQueries;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/4
 */
public interface QuestionService {
    Optional<String> uploadUserQuestionBank(String username, String password);

    Optional<Question> getQuestion(String id);

    Page<Question> list(QuestionQueries queries);

    List<String> getAllQuestionType();

    Question saveQuestion(Question question);

    List<Question> batchSaveQuestion(Iterable<Question> questions);
}
