package com.question;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.question.api.ApiService;
import com.question.model.Question;
import com.question.model.Subject;
import com.question.model.User;
import com.question.parse.HtmlParse;
import com.question.queries.QuestionQueries;
import com.question.service.QuestionService;
import com.question.service.SubjectService;
import com.question.service.UserService;
import com.question.utils.concurrent.QuestionExecutorService;
import com.question.utils.stream.CompletableFutureCollector;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@Slf4j
public class QuestionServiceApplicationTests {
    @Resource
    private ApiService apiService;
    @Resource
    private HtmlParse parse;
    @Resource
    private QuestionService questionService;
    @Resource
    private SubjectService subjectService;
    @Resource
    private UserService userService;

    //    @Test
    public void contextLoads() throws ExecutionException, InterruptedException {
//        User user = userService.getUser("16212116009").get();
//        System.out.println(user);
        String cookie = "zdyj2web=BB15AB56E61D023B8D57F88F87F391F3";
//        final CompletableFuture<List<Class<Void>>> collect = aLlSubjectName.stream().map(subject -> {
        Subject subject = subjectService.getSubject("171121811258009").get();

//        QuestionQueries questionQueries = new QuestionQueries();
//        questionQueries.setSubject(subject.getName());
        System.out.println(subject);
        final String questionsHtml = apiService.getSubjectQuestions(subject.getId(), cookie).getBody();
        System.out.println("查询试题列表 {}" + questionsHtml);

        List<JSONObject> maps = parse.parseQuestions(questionsHtml);
        System.out.println("解析试题列表 size:{}, {}" + maps.size() + JSON.toJSONString(maps));
        CompletableFuture.supplyAsync(() -> {
            maps.forEach(map -> QuestionExecutorService.executorService.submit(() -> {

                String id = map.getString("id");
                String type = map.getString("type");
                try {
                    String questionHtml = apiService.getQuestion(id, cookie).getBody();
                    log.info("查询试题 {}", questionHtml);
                    Question question = parse.parseQuestion(questionHtml);

                    question.setId(id);
                    question.setOwnerSubject(subject.getName());
                    question.setOwnerSpecialty(subject.getOwnerSpecialty());
                    question.setType(type);
                    question.setCreatedUsername(subject.getCreatedUsername());
                    log.info("解析试题 {}", JSON.toJSONString(question));

                    questionService.saveQuestion(question);

                } catch (Exception e) {
                    log.error("保存试题失败 message: {}, e:{} ", e.getMessage(), e);
                }
            }));
            return Void.TYPE;
        }).get();
        Thread.sleep(3000000L);
//        }).collect(CompletableFutureCollector.collectResult());
//        collect.get();
    }

}
