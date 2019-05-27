package com.queries;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.utils.CompletableFutureCollector;
import com.common.utils.Either;
import com.queries.api.ApiService;
import com.queries.enums.ProblemStatus;
import com.queries.enums.ProblemType;
import com.queries.events.AutomaticOnlineTestPaperEvent;
import com.queries.events.UploadQueriesEvent;
import com.queries.events.UserQuestion;
import com.queries.exceptions.ApiException;
import com.queries.exceptions.QueriesServiceException;
import com.queries.exceptions.RestApiException;
import com.queries.models.*;
import com.queries.parses.TestPaper;
import com.queries.parses.TestPaperResult;
import com.queries.services.CourseService;
import com.queries.services.ProblemService;
import com.queries.services.QuestionService;
import com.queries.utils.cache.CacheService;
import com.queries.utils.concurrent.QuestionExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * @author wanghongen
 * 2019-03-14
 */
@Slf4j
@Component
public class Crawling {
    @Resource
    private CourseService courseService;
    @Resource
    private QuestionService questionService;
    @Resource
    private CacheService cacheService;
    @Resource
    private ProblemService problemService;
    @Resource
    private ApiService apiService;

    @Async
    @EventListener
    public void uploadQueriesEventHandle(UploadQueriesEvent uploadQueriesEvent) {
        log.info("上传题库事件 [{}]", JSONObject.toJSONString(uploadQueriesEvent.getSource()));
        UserQuestion source = uploadQueriesEvent.getSource();

        crawlingQuestionBank(source.getToken(), source.getUser());
    }

    /**
     * 爬取用户题库
     */
    private void crawlingQuestionBank(String token, User user) {
        //正在爬取跳过
        if (cacheService.setIfAbsent(token, user) != null) {
            return;
        }

        //根据token登陆考试系统
        apiService.questionBankLogin(token).fold(e -> {
            //token无效重试一次
            if (e.getErrorCode().equals(QueriesServiceException.IllegalTokenException.build().getErrorCode())) {
                return apiService.login(user.getUsername(), user.getPassword()).flatMap(userToken -> apiService.questionBankLogin(userToken).fold(ex -> Optional.empty(), Optional::ofNullable));
            }

            return Optional.<String>empty();
        }, Optional::ofNullable).ifPresent(cookie -> {

            Set<Course> courses = apiService.getCourses(cookie);

            log.info("解析课程列表 {} [{}]", cookie, JSON.toJSONString(courses));
            courses.stream()
                    .filter(course -> !courseService.containsCourse(course.getName()))
                    .map(course ->
                            CompletableFuture.runAsync(() -> {
                                courseService.saveCourse(course, user);
                                List<JSONObject> maps = apiService.getCourseQuestions(course.getId(), cookie);
                                log.info("解析试题列表 {} [size:{}, {}]", cookie, maps.size(), JSON.toJSONString(maps));
                                maps.forEach(map -> {
                                    String id = map.getString("id");
                                    try {
                                        crawlingQuestion(id, user.getUsername(), course.getName(), cookie);
                                    } catch (Exception e) {
                                        log.error("保存试题失败 message: {}, e:{} ", e.getMessage(), e);
                                    }
                                });
                            }, QuestionExecutorService.executorService))
                    .collect(CompletableFutureCollector.collectResult())
                    .thenRun(() -> cacheService.remove(user.getUsername()));
        });

    }

    /**
     * 爬取问题答案
     *
     * @param qid       ID
     * @param username  用户名
     * @param course    课程
     * @param sessionId 会话ID
     */
    private Either<RestApiException, Question> crawlingQuestion(String qid, String username, String course, String sessionId) {
        return apiService.getQuestion(qid, sessionId).mapRight(question -> {
            question.setCourse(course);
            question.setCreatedUsername(username);
            log.debug("解析试题 {} [{}]", sessionId, JSON.toJSONString(question));
            return questionService.saveQuestion(question);
        });
    }

    /**
     * 自动在线测试事件处理
     */
    @Async
    @EventListener
    public void automaticOnlineTestPaperEventHandle(AutomaticOnlineTestPaperEvent event) {
        crawlingCourseTestPaper(event.getSource());
    }

    /**
     * 自动在线测试
     *
     * @param token token
     */
    public void automaticOnlineTestPaper(String token) {
        apiService.getUserInfo(token).ifPresent(user -> crawlingCourseTestPaper(new UserQuestion(token, user)));
    }


    /**
     * 爬取用户试题
     */
    private void crawlingCourseTestPaper(UserQuestion userQuestion) {
        final User user = userQuestion.getUser();
        log.info("开始自动答题 [username:{}]", user.getUsername());
        apiService.learningCourses(userQuestion.getToken())
                .forEach(course -> {
                    final List<TestPaper> testPaperList = course.getTestPaperList();
                    if (testPaperList != null) {
                        course.getTestPaperList()
                                .stream()
                                .filter(testPaper -> testPaper.getScore() < 20)
                                .forEach(testPaper -> {
                                    log.info("自动答题 [username: {}, token:{}, course:{}]", user.getUsername(), testPaper.getToken(), course.getName());
                                    crawlingTestPaper(user.getUsername(), course.getName(), testPaper);
                                });
                    }
                });
    }


    private void crawlingTestPaper(String username, String course, TestPaper testPaper) {
        crawlingTestPaper(username, course, testPaper.getToken(), testPaper.getPaperId(), testPaper.getRuid());
    }

    void crawlingTestPaper(String username, String course, String token, String paperId, String ruid) {
        final List<Problem> problems = apiService.getTestPaper(token, paperId, ruid);
        String sessionId = cacheService.get(username, () -> {
            final Either<ApiException, String> apiExceptionStringEither = apiService.questionBankLogin(token);
            return apiExceptionStringEither.getRight();
        });

        final Map<String, String> collect = problems.stream()
                .map(problem -> {
                    final String id = problem.getId();
                    return questionService.getQuestion(id, problem.getQuestion())
                            .filter(it -> StringUtils.isNotBlank(it.getAnswer()))
                            .map(Either::<RestApiException, Question>right)
                            .orElseGet(() -> crawlingQuestion(id, username, course, sessionId).fold(e -> {
                                        log.info("爬取试题失败, 保存问题记录 [id:{}, title:{}, question:{}]", id, problem.getTitle(), problem.getQuestion());
                                        //爬取失败 判断问题是否回答 保存问题
                                        problem.setCreateUsername(username);
                                        problem.setCourse(course);
                                        final Problem p = problemService.getProblemOrCreate(problem);
                                        if (p.getStatus().equals(ProblemStatus.SUBMITTED.getStatus())) {
                                            final Question question = new Question();
                                            final Optional<Answer> answer = p.getAnswers().stream().filter(Answer::isAnswerRight).findAny();
                                            question.setId(p.getId());
                                            question.setAnswers(p.getAnswers());
                                            question.setAnswer(answer.map(Answer::getAnswer).orElse(null));
                                            return Either.<RestApiException, Question>right(question);
                                        }
                                        return Either.left(e);
                                    }, Either::right)
                            ).fold(e -> Collections.<String, String>emptyMap(), question -> {
                                //多选题
                                if (ProblemType.MULTIPLE_CHOICE.getLabel().equals(problem.getType())) {
                                    final Set<String> answers = question.getAnswers()
                                            .stream()
                                            .filter(Answer::isAnswerRight)
                                            .map(answer -> answer.getAnswer().replaceAll("，", ","))
                                            .collect(Collectors.toSet());

                                    return problem.getAnswers().stream()
                                            .filter(answer -> answers.contains(answer.getAnswer().replace("，", ",")))
                                            .collect(toMap(answer -> id + answer.getSymbol(), Answer::getSymbol));
                                }

                                return problem.getAnswers().stream()
                                        .filter(answer -> question.getAnswer().equals(answer.getAnswer().trim()))
                                        .findAny().map(answer -> Collections.singletonMap(id, answer.getSymbol()))
                                        .orElseGet(() -> {
                                            final Set<String> answerSet = problem.getAnswers().stream().map(Answer::getAnswer).collect(Collectors.toSet());
                                            log.warn("匹配答案失败 [{}, question:{}, answer:{}, problemAnswers:{}]", id, problem.getQuestion(), question.getAnswer(), answerSet);
                                            return Collections.emptyMap();
                                        });
                            });
                })
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (old, newVal) -> newVal));

        //提交试题
        apiService.submitTestPaper(token, username + paperId, ruid, collect, paperResult -> paperResultHandle(paperResult, paperId, problems, collect));
    }


    private void paperResultHandle(TestPaperResult paperResult, String paperId, List<Problem> problems, Map<String, String> collect) {
        log.info("提交试题返回结果 [paperId: {}, paperResult: {}]", paperId, paperResult.getTotalScore());
        final List<Boolean> results = paperResult.getResults();
        for (int i = 0; i < results.size(); i++) {
            final Problem p = problems.get(i);
            final Boolean isRight = results.get(i);
            if (!isRight) {
                log.info("回答错误 [{} paperId: {}, question: {}, answer:{}]", p.getId(), paperId, p.getQuestion(), collect.get(p.getId()));
            }
            problemService.getProblem(p.getId()).ifPresent(problem -> {

                if (problem.getStatus().equals(ProblemStatus.SUBMITTED.getStatus())) {
                    //是提交状态 回答正确 改为完成状态 保存试题
                    if (isRight)
                        problemService.finish(problem);
                    else {
                        problemService.open(problem);
                    }
                }
            });
        }
    }
}
