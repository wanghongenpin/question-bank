package com.queries.services.impl;

import com.queries.dao.QuestionRepository;
import com.queries.models.Answer;
import com.queries.models.Question;
import com.queries.request.QuestionQuery;
import com.queries.services.AnswerService;
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
    private AnswerService answerService;

//    private String courseExcludeKey = "course:crawling:exclude";

    @Override
    public Optional<Question> getQuestion(String id) {
        return questionRepository.findById(id).map(question -> {
            final List<Answer> answers = answerService.findAnswerByQuestionId(question.getId());
            question.setAnswers(answers);
            return question;
        });
    }

    @Override
    public Optional<Question> getQuestion(String id, String question, String typeDescribe) {
        final Optional<Question> optionalQuestion = getQuestion(id);
        return optionalQuestion.isPresent() ? optionalQuestion : getByQuestion(question, typeDescribe);
    }

    private Optional<Question> getByQuestion(String question, String typeDescribe) {
        return questionRepository.findFirstByQuestionContainsAndTypeDescribe(question, typeDescribe).map(q -> {
            final List<Answer> answers = answerService.findAnswerByQuestionId(q.getId());
            q.setAnswers(answers);
            return q;
        });
    }

    @Override
    public Page<Question> list(QuestionQuery query) {
        Question question = new Question();
        if (query.getQuestion() != null) {
            question.setQuestion(query.getQuestion());
        }
        if (query.getCourse() != null) {
            question.setCourse(query.getCourse());
        }
        if (query.getQuestionType() != null) {
            question.setTypeDescribe(query.getQuestionType());
        }
        if (query.getAnswer() != null) {
            question.setAnswer(query.getAnswer());
        }
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("question", ExampleMatcher.GenericPropertyMatchers.contains());
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
        save.setAnswers(answerService.batchSaveAnswer(answers));
        return save;
    }

    @Override
    public List<Question> batchSaveQuestion(Iterable<Question> questions) {
        return questionRepository.saveAll(questions);
    }


}
