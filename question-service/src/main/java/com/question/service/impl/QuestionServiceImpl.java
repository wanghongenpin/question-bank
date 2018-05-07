package com.question.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.question.api.ApiService;
import com.question.dao.AnswerRepository;
import com.question.dao.QuestionRepository;
import com.question.model.Answer;
import com.question.model.Question;
import com.question.model.Subject;
import com.question.model.User;
import com.question.parse.HtmlParse;
import com.question.queries.QuestionQueries;
import com.question.service.QuestionService;
import com.question.service.SubjectService;
import com.question.service.UserService;
import com.question.utils.cache.CacheService;
import com.question.utils.concurrent.QuestionExecutorService;
import com.question.utils.stream.CompletableFutureCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
    @Resource
    private ApiService apiService;
    @Resource
    private UserService userService;
    @Resource
    private SubjectService subjectService;
    @Resource
    private CacheService cacheService;
    @Resource
    private HtmlParse parse;

    @Override
    public Optional<String> uploadUserQuestionBank(String username, String password) {
        ResponseEntity<String> loginResponse = apiService.login(username, password);
        String body = loginResponse.getBody();
        log.info("登录返回 username: {}, body: {}", username, body);
        Optional<String> tokenOptional = parse.parseToken(body);
        log.info("解析token  username: {}, token: {}", username, tokenOptional);
        return tokenOptional.map(token -> {
            cacheService.set(username, token);
            this.crawlingQuestionBank(username, password, token);
            return token;
        });
    }

    @Override
    public Optional<Question> getQuestion(String id) {
        return questionRepository.findById(id);
    }

    @Override
    public Page<Question> list(QuestionQueries queries) {
        Question question = new Question();
        if (queries.getTitle() != null) {
            question.setTitle(queries.getTitle());
        }
        if (queries.getSubject() != null) {
            question.setOwnerSubject(queries.getSubject());
        }
        if (queries.getQuestionType() != null) {
            question.setTypeDescribe(queries.getQuestionType());
        }
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Question> example = Example.of(question, matcher);
        Pageable pageable = PageRequest.of(queries.getPage(), queries.getSize());
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

    private void crawlingQuestionBank(String username, String password, String token) {
        String userHtml = apiService.getUserInfo(token).getBody();
        log.info("查询用户信息 username={}, html={}", username, userHtml);
        User user = parse.parseUser(userHtml).map(u -> {
            log.info("解析用户 user: {}", JSON.toJSONString(u));
            u.setPassword(password);
            return userService.saveUser(u);
        }).orElseThrow(() -> {
            cacheService.remove(username);
            return new RuntimeException("无效token");
        });

        CompletableFuture.supplyAsync(() -> apiService.subjectBankLogin(token))
                .thenApply((cookie) -> {
                    String subjectListHtml = apiService.getSubjects(cookie).getBody();
                    log.info("查询学科列表 {}", subjectListHtml);
                    Set<Subject> subjects = parse.parseSubjectList(subjectListHtml);

                    log.info("解析学科列表 {}", JSON.toJSONString(subjects));

                    return subjects.stream().map(subject -> {
                        subject.setCreatedUsername(user.getUsername());
                        subject.setOwnerSpecialty(user.getSpecialty());
                        subjectService.saveSubject(subject);
                        apiService.getSubject(subject.getId(), cookie);

                        String questionsHtml = apiService.getQuestions(cookie).getBody();
                        log.info("查询试题列表 {}", questionsHtml);

                        List<JSONObject> maps = parse.parseQuestions(questionsHtml);
                        log.info("解析试题列表 size:{}, {}", maps.size(), JSON.toJSONString(maps));
                        return CompletableFuture.supplyAsync(() -> {
                            maps.forEach(map -> {
                                String id = map.getString("id");
                                String type = map.getString("type");
                                try {
                                    String questionHtml = apiService.getQuestion(id, cookie).getBody();
                                    log.info("查询试题 {}", questionHtml);
                                    Question question = parse.parseQuestion(questionHtml);
                                    log.info("解析试题 {}", JSON.toJSONString(question));

                                    question.setId(id);
                                    question.setOwnerSubject(subject.getName());
                                    question.setOwnerSpecialty(user.getSpecialty());
                                    question.setType(type);
                                    question.setCreatedUsername(user.getUsername());
                                    this.saveQuestion(question);

                                } catch (Exception e) {
                                    log.error("保存试题失败 message: {}, e:{} ", e.getMessage(), e);
                                }
                            });
                            return Void.TYPE;
                        }, QuestionExecutorService.executorService);
                    }).collect(CompletableFutureCollector.collectResult())
                            .thenAccept((result) -> cacheService.remove(username));
                });
    }
}
