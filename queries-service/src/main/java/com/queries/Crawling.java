package com.queries;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.utils.CompletableFutureCollector;
import com.queries.api.ApiService;
import com.queries.events.UploadQueriesEvent;
import com.queries.models.Question;
import com.queries.models.Subject;
import com.queries.models.User;
import com.queries.parses.HtmlParse;
import com.queries.services.QuestionService;
import com.queries.services.SubjectService;
import com.queries.services.UserService;
import com.queries.utils.cache.CacheService;
import com.queries.utils.concurrent.QuestionExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @author wanghongen
 * 2019-03-14
 */
@Slf4j
@Component
public class Crawling {
    @Resource
    private UserService userService;
    @Resource
    private SubjectService subjectService;
    @Resource
    private QuestionService questionService;
    @Resource
    private CacheService cacheService;
    @Resource
    private ApiService apiService;
    @Resource
    private HtmlParse parse;

    @Async
    @EventListener
    public void uploadQueriesEventHandle(UploadQueriesEvent uploadQueriesEvent) {
        log.info("上传题库事件 [{}]", JSONObject.toJSONString(uploadQueriesEvent.getSource()));
        UploadQueriesEvent.UserQueries source = uploadQueriesEvent.getSource();

        crawlingQuestionBank(source.getToken(), source.getUsername(), source.getPassword());
    }

    private void crawlingQuestionBank(String token, String username, String password) {

        String userHtml = apiService.getUserInfo(token).getBody();
        Optional<User> user = parse.parseUser(userHtml);
        if (!user.isPresent()) {
            log.warn("解析用户失败 [token: {}, username: {}, password: {}]", token, username, password);
        }

        user.ifPresent(u -> {
            log.info("解析用户 user: {}", JSON.toJSONString(u));
            u.setPassword(password);
            userService.saveUser(u);
            cacheService.set(username, token);

            String cookie = apiService.subjectBankLogin(token);
            String subjectListHtml = apiService.getSubjects(cookie).getBody();
            Set<Subject> subjects = parse.parseSubjectList(subjectListHtml);

            log.info("解析学科列表 {}", JSON.toJSONString(subjects));
            subjects.stream()
                    .filter(subject -> !subjectService.containsSubject(subject.getName()))
                    .map(subject ->
                            CompletableFuture.runAsync(() -> {
                                subject.setCreatedUsername(username);
                                subject.setOwnerSpecialty(u.getSpecialty());
                                subjectService.saveSubject(subject);
                                String questionsHtml = apiService.getSubjectQuestions(subject.getId(), cookie).getBody();

                                List<JSONObject> maps = parse.parseQuestions(questionsHtml);
                                log.info("解析试题列表 size:{}, {}", maps.size(), JSON.toJSONString(maps));
                                maps.forEach(map -> {
                                    String id = map.getString("id");
                                    String type = map.getString("type");
                                    try {
                                        String questionHtml = apiService.getQuestion(id, cookie).getBody();
                                        Question question = parse.parseQuestion(questionHtml);

                                        question.setId(id);
                                        question.setOwnerSubject(subject.getName());
                                        question.setOwnerSpecialty(u.getSpecialty());
                                        question.setType(type);
                                        question.setCreatedUsername(u.getUsername());
                                        log.info("解析试题 {}", JSON.toJSONString(question));
                                        questionService.saveQuestion(question);

                                    } catch (Exception e) {
                                        log.error("保存试题失败 message: {}, e:{} ", e.getMessage(), e);
                                    }
                                });
                            }, QuestionExecutorService.executorService))
                    .collect(CompletableFutureCollector.collectResult())
                    .thenRun(() -> cacheService.remove(username));

        });

    }
}
