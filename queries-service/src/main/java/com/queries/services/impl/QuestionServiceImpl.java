package com.queries.services.impl;

import com.queries.dao.AnswerRepository;
import com.queries.dao.QuestionRepository;
import com.queries.models.Answer;
import com.queries.models.Question;
import com.queries.request.QuestionQuery;
import com.queries.services.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author wanghongen
 * 2018/5/4
 */
@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService {
    @Resource
    private QuestionRepository questionRepository;
    @Resource
    private AnswerRepository answerRepository;

//    private String subjectExcludeKey = "subject:crawling:exclude";

    @Override
    public Optional<Question> getQuestion(String id) {
        return questionRepository.findById(id);
    }

    @Override
    public Page<Question> list(QuestionQuery query) {
        Question question = new Question();
        if (query.getTitle() != null) {
            question.setTitle(query.getTitle());
        }
        if (query.getSubject() != null) {
            question.setOwnerSubject(query.getSubject());
        }
        if (query.getQuestionType() != null) {
            question.setTypeDescribe(query.getQuestionType());
        }
        if (query.getAnswer() != null) {
            question.setAnswer(query.getAnswer());
        }
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Question> example = Example.of(question, matcher);
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        return questionRepository.findAll(example, pageable);
    }

    @Override
    public List<String> getAllQuestionType() {
        return questionRepository.getAllQuestionType();
    }

    @Override
    public Question saveQuestion(Question question) {
        LocalDateTime now = LocalDateTime.now();
        question.setCreatedTime(now);
        question.setUpdatedTime(now);
        Question save = questionRepository.save(question);
        List<Answer> answers = question.getAnswers()
                .stream()
                .peek(answer -> answer.setQuestionId(save.getId()))
                .collect(toList());
        save.setAnswers(answerRepository.saveAll(answers));
        return save;
    }

    @Override
    public List<Question> batchSaveQuestion(Iterable<Question> questions) {
        return questionRepository.saveAll(questions);
    }


}
