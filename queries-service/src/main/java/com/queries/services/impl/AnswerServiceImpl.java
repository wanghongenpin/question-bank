package com.queries.services.impl;

import com.queries.dao.AnswerRepository;
import com.queries.models.Answer;
import com.queries.services.AnswerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/5
 */
@Service
public class AnswerServiceImpl implements AnswerService {
    @Resource
    private AnswerRepository answerRepository;

    @Override
    public Optional<Answer> getAnswer(String id) {
        return answerRepository.findById(id);
    }

    @Override
    public Answer saveAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    public List<Answer> batchSaveAnswer(List<Answer> answers) {
        return answerRepository.saveAll(answers);
    }

    @Override
    public List<Answer> findAnswerByQuestionId(String questionId) {
        return answerRepository.findAnswerByQuestionId(questionId);
    }

}
