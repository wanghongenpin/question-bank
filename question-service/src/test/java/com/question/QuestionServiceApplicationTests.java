package com.question;

import com.alibaba.fastjson.JSON;
import com.question.api.ApiService;
import com.question.model.Question;
import com.question.parse.HtmlParse;
import com.question.queries.QuestionQueries;
import com.question.service.QuestionService;
import com.question.service.SubjectService;
import com.question.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingDeque;

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
    public void contextLoads() throws InterruptedException {
//        User user = userService.getUser("16212116009").get();
//        System.out.println(user);
        String cookie = "zdyj2web=FB25FE7464977406CB2A836FEBD22C13";
        QuestionQueries questionQueries = new QuestionQueries();
        questionQueries.setQuestionType("多选题");
        questionQueries.setAnswer("");
        questionQueries.setSize(200);
        questionQueries.setPage(0);
        final Page<Question> list = questionService.list(questionQueries);
        Collection<Question> questions = new LinkedBlockingDeque<>();


        list.getContent().parallelStream().forEach(q -> {

            try {

                String questionHtml = apiService.getQuestion(q.getId(), cookie).getBody();
//                    log.info("查询试题 {}", questionHtml);
                Question question = parse.parseQuestion(questionHtml);

                question.setId(q.getId());
                question.setOwnerSubject(q.getOwnerSubject());
                question.setOwnerSpecialty(q.getOwnerSpecialty());
                question.setType(q.getType());
                question.setCreatedUsername(q.getCreatedUsername());
                log.info("解析试题 {}", JSON.toJSONString(question));
//                questions.add(question);
                questionService.saveQuestion(question);

            } catch (Exception e) {
                log.error("保存试题失败 message: {}, e:{} ", e.getMessage(), e);
            }
        });
//        final CompletableFuture<List<Class<Void>>> collect = aLlSubjectName.stream().map(subject -> {
//        Subject subject = subjectService.getSubject("162035270607525").get();

//        QuestionQueries questionQueries = new QuestionQueries();
//        questionQueries.setSubject(subject.getName());
//        System.out.println(subject);
//        final String questionsHtml = apiService.getSubjectQuestions(subject.getId(), cookie).getBody();
//        System.out.println("查询试题列表 {}" + questionsHtml);

//        List<JSONObject> maps = parse.parseQuestions(questionsHtml);
//        System.out.println("解析试题列表 size:{}, {}" + maps.size() + JSON.toJSONString(maps));
//        CompletableFuture.supplyAsync(() -> {
//            maps.forEach(map -> QuestionExecutorService.executorService.submit(() -> {
//
//                String id = map.getString("id");
//                String type = map.getString("type");
//                try {
//                    String questionHtml = apiService.getQuestion(id, cookie).getBody();
//                    log.info("查询试题 {}", questionHtml);
//                    Question question = parse.parseQuestion(questionHtml);
//
//                    question.setId(id);
//                    question.setOwnerSubject(subject.getName());
//                    question.setOwnerSpecialty(subject.getOwnerSpecialty());
//                    question.setType(type);
//                    question.setCreatedUsername(subject.getCreatedUsername());
//                    log.info("解析试题 {}", JSON.toJSONString(question));
//
//                    questionService.saveQuestion(question);
//
//                } catch (Exception e) {
//                    log.error("保存试题失败 message: {}, e:{} ", e.getMessage(), e);
//                }
//            }));
//            return Void.TYPE;
//        }).get();
//        Thread.sleep(300000L);
//        }).collect(CompletableFutureCollector.collectResult());
//        collect.get();
    }

    private void save(Collection<Question> questions) {
        if (questions.size() >= 50) {
            questionService.batchSaveQuestion(questions);
        }
    }

}
