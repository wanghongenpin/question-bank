package com.queries;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.utils.CompletableFutureCollector;
import com.queries.api.ApiService;
import com.queries.events.UploadQueriesEvent;
import com.queries.exceptions.QueriesServiceException;
import com.queries.models.Question;
import com.queries.models.Subject;
import com.queries.models.User;
import com.queries.parses.HtmlParse;
import com.queries.services.QuestionService;
import com.queries.services.SubjectService;
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

        crawlingQuestionBank(source.getToken(), source.getUser());
    }

    private void crawlingQuestionBank(String token, User user) {
        //正在爬取跳过
        if (cacheService.setIfAbsent(token, user) != null) {
            return;
        }

        //根据token登陆考试系统
        apiService.subjectBankLogin(token).fold(e -> {
            //token无效重试一次
            if (e.getErrorCode().equals(QueriesServiceException.IllegalTokenException.build().getErrorCode())) {
                String loginHtml = apiService.login(user.getUsername(), user.getPassword()).getBody();
                return parse.parseToken(loginHtml).flatMap(userToken -> apiService.subjectBankLogin(userToken).fold(ex -> Optional.empty(), Optional::ofNullable));
            }

            return Optional.<String>empty();
        }, Optional::ofNullable).ifPresent(cookie -> {

            String subjectListHtml = apiService.getSubjects(cookie).getBody();
            Set<Subject> subjects = parse.parseSubjectList(subjectListHtml);

            log.info("解析学科列表 {} [{}]", cookie, JSON.toJSONString(subjects));
            subjects.stream()
                    .filter(subject -> !subjectService.containsSubject(subject.getName()))
                    .map(subject ->
                            CompletableFuture.runAsync(() -> {
                                subjectService.saveSubject(subject, user);
                                String questionsHtml = apiService.getSubjectQuestions(subject.getId(), cookie).getBody();

                                List<JSONObject> maps = parse.parseQuestions(questionsHtml);
                                log.info("解析试题列表 {} [size:{}, {}]", cookie, maps.size(), JSON.toJSONString(maps));
                                maps.forEach(map -> {
                                    String id = map.getString("id");
                                    String type = map.getString("type");
                                    try {
                                        String questionHtml = apiService.getQuestion(id, cookie).getBody();
                                        Question question = parse.parseQuestion(questionHtml);

                                        question.setId(id);
                                        question.setOwnerSubject(subject.getName());
                                        question.setOwnerSpecialty(user.getSpecialty());
                                        question.setType(type);
                                        question.setCreatedUsername(user.getUsername());
                                        log.info("解析试题 {} [{}]", cookie, JSON.toJSONString(question));
                                        questionService.saveQuestion(question);

                                    } catch (Exception e) {
                                        log.error("保存试题失败 message: {}, e:{} ", e.getMessage(), e);
                                    }
                                });
                            }, QuestionExecutorService.executorService))
                    .collect(CompletableFutureCollector.collectResult())
                    .thenRun(() -> cacheService.remove(user.getUsername()));
        });

    }
}
