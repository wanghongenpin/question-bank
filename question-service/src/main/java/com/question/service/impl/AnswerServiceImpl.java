package com.question.service.impl;

import com.question.dao.AnswerRepository;
import com.question.model.Answer;
import com.question.service.AnswerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wanghongen
 * 2018/5/5
 */
@Service
public class AnswerServiceImpl implements AnswerService {
    @Resource
    private AnswerRepository answerRepository;

    public List<Answer> batchSaveAnswer(List<Answer> answers) {
        return answerRepository.saveAll(answers);
    }

}
