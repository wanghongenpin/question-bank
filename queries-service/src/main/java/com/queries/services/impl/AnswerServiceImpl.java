package com.queries.services.impl;

import com.queries.dao.AnswerRepository;
import com.queries.models.Answer;
import com.queries.services.AnswerService;
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
