package com.queries.services.impl;

import com.queries.dao.ProblemRepository;
import com.queries.enums.ProblemStatus;
import com.queries.events.ProblemEvent;
import com.queries.models.Answer;
import com.queries.models.Problem;
import com.queries.models.Question;
import com.queries.request.ProblemSubmit;
import com.queries.services.AnswerService;
import com.queries.services.ProblemService;
import com.queries.services.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author wanghongen
 * 2019-05-22
 */
@Slf4j
@Service
public class ProblemServiceImpl implements ProblemService {
    private final ProblemRepository problemRepository;

    @Resource
    private AnswerService answerService;
    @Resource
    private QuestionService questionService;

    public ProblemServiceImpl(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @EventListener
    public void problemEventHandle(ProblemEvent problemEvent) {
        final Problem source = problemEvent.getSource();
        create(source);
    }

    @Override
    public Problem create(Problem problem) {
        problem.setCreateTime(LocalDateTime.now());
        problem.setStatus(ProblemStatus.OPEN.getStatus());
        answerService.batchSaveAnswer(problem.getAnswers());
        return problemRepository.save(problem);
    }

    @Override
    public Problem getProblemOrCreate(Problem problem) {
        return getProblem(problem.getId()).orElseGet(() -> create(problem));
    }

    @Override
    public Optional<Problem> getProblem(String id) {
        return problemRepository.findById(id).map(problem -> {
            final List<Answer> answers = answerService.findAnswerByQuestionId(problem.getId());
            problem.setAnswers(answers);
            return problem;
        });
    }

    @Override
    public void open(Problem problem) {
        getProblem(problem.getId()).ifPresent(it -> {
            it.setStatus(ProblemStatus.OPEN.getStatus());
            problemRepository.save(it);
        });
    }

    @Override
    public void submit(ProblemSubmit problemSubmit) {
        getProblem(problemSubmit.getId())
                .filter(problem -> problem.getStatus().equals(ProblemStatus.OPEN.getStatus()))
                .ifPresent(problem -> {
                    log.info("提交问题 [{}]", problemSubmit);
                    //提交状态
                    problem.setStatus(ProblemStatus.SUBMITTED.getStatus());
                    //修改正确回答
                    problemSubmit.getAnswerIds().forEach(id ->
                            answerService.getAnswer(id).ifPresent(answer -> {
                                answer.setAnswerRight(true);
                                answerService.saveAnswer(answer);
                            })
                    );
                    problemRepository.save(problem);
                });
    }

    @Override
    public void finish(Problem problem) {
        getProblem(problem.getId()).ifPresent(p -> {
            p.setStatus(ProblemStatus.FINISHED.getStatus());
            problemRepository.save(p);
            AtomicInteger count = new AtomicInteger(0);
            final List<Answer> answers = p.getAnswers().stream().filter(Answer::isAnswerRight).collect(Collectors.toList());
            if (answers.isEmpty()) {
                return;
            }
            String answer;
            if (answers.size() > 1) {
                answer = answers.stream().map(it -> count.incrementAndGet() + "." + it.getAnswer()).collect(Collectors.joining("<br/>"));
            } else {
                answer = answers.get(0).getAnswer();
            }
            //保存试题
            final Question question = Question.builder().id(p.getId()).question(p.getQuestion()).answers(p.getAnswers())
                    .answer(answer).typeDescribe(p.getType()).course(p.getCourse()).createdUsername(p.getCreateUsername()).build();
            questionService.saveQuestion(question);
        });
    }
}
