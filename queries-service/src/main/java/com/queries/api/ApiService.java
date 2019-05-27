package com.queries.api;

import com.alibaba.fastjson.JSONObject;
import com.common.utils.Either;
import com.common.utils.Try;
import com.queries.exceptions.ApiException;
import com.queries.exceptions.QueriesServiceException;
import com.queries.exceptions.RestApiException;
import com.queries.models.Course;
import com.queries.models.Problem;
import com.queries.models.Question;
import com.queries.models.User;
import com.queries.parses.HtmlParse;
import com.queries.parses.LearningCourses;
import com.queries.parses.TestPaperResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author wanghongen
 * 2018/5/3
 */
@Slf4j
@Service
public class ApiService {
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private HtmlParse parse;
    @Resource
    private ApiConfiguration configuration;
    //提交答案需要延迟2分钟
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());


    public Optional<String> login(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("uid", username);
        param.add("pw", password);
        param.add("B1", "+%B5%C7%C2%BC+");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        final String loginHtml = restTemplate.postForObject(configuration.getLoginUrl(), request, String.class);
        return parse.parseToken(loginHtml);
    }

    /**
     * 获取学习课程考试列表
     *
     * @param token TOKEN
     */
    public List<LearningCourses> learningCourses(String token) {
        final String url = configuration.getHomepageHost() + "/vls5s/vls3isapi2.dll/getfirstpage?ptopid=" + token;
        final String homepageHtml = restTemplate.getForObject(url, String.class);
        return parse.parseLearningCourses(homepageHtml);
    }

    public Optional<User> getUserInfo(String token) {
        return parse.parseUser(restTemplate.getForObject(configuration.getUserInfoUrl(), String.class, token));
    }

    /**
     * 使用cookie来实现登陆的
     * 一个token对应cookie
     */
    public Either<ApiException, String> questionBankLogin(String token) {

        return Try.apply(() -> {

            URL url = URI.create(configuration.getQuestionBankLoginUrl() + token).toURL();
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            urlConnection.getInputStream().close();
            //获取重定向URL上cookie
            String path = urlConnection.getURL().getPath();
            int indexOf = path.lastIndexOf(";");
            if (indexOf < 0) {
                return Either.<ApiException, String>left(QueriesServiceException.IllegalTokenException.build());
            }
            return Either.<ApiException, String>right(path.substring(indexOf + 1));
        }).recover(e -> {
            e.printStackTrace();
            return Either.left(new RestApiException("RA5001", "登陆失败," + e.getMessage()));
        }).get();
    }

    public Set<Course> getCourses(String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        HttpEntity entity = new HttpEntity(headers);
        final ResponseEntity<String> responseEntity = restTemplate.exchange(configuration.getCoursesUrl(), HttpMethod.GET, entity, String.class);
        return parse.parseSubjectList(responseEntity.getBody());
    }

    public List<JSONObject> getCourseQuestions(String id, String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        HttpEntity entity = new HttpEntity(headers);
        final ResponseEntity<String> responseEntity = restTemplate.exchange(configuration.getCourseQuestionsUrl(), HttpMethod.GET, entity, String.class, id);
        return parse.parseQuestions(responseEntity.getBody());
    }

    public Either<RestApiException, Question> getQuestion(String id, String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        HttpEntity entity = new HttpEntity(headers);
        final ResponseEntity<String> responseEntity = restTemplate.exchange(configuration.getQuestionUrl(), HttpMethod.GET, entity, String.class, id);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return Either.right(parse.parseQuestion(responseEntity.getBody()));
        } else {
            return Either.left(new RestApiException(responseEntity.getStatusCodeValue() + "", responseEntity.getBody()));
        }
    }

    /**
     * 获取试题问题
     */
    public List<Problem> getTestPaper(String token, String id, String ruid) {
        //进入开始测试  貌似没什么用
//        final String url = configuration.getHomepageHost() + "/vls2s/vls3isapi.dll/testonce0?ptopid=" + token + "&ruid=" + ruid + "&zhang=" + id;
//        restTemplate.getForObject(url, String.class);

        //开始考试
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("ptopid", token);
        params.add("zhang", id);
        params.add("ruid", ruid);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        final String paperHtml = restTemplate.postForObject(configuration.getTestPaperUrl(), entity, String.class);
        return parse.parseTestPaperProblems(paperHtml);
    }

    /**
     * 提交试卷
     * 提交答案需要延迟120
     */
    public void submitTestPaper(String token, String paperId, String ruid, Map<String, String> answers, Consumer<TestPaperResult> action) {
        //提交答案需要延迟120
        final int delay = ThreadLocalRandom.current().nextInt(120, 140);

        SCHEDULED_EXECUTOR.schedule(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.setAll(answers);
            params.add("ptopid", token);
            params.add("paperid", paperId);
            params.add("ruid", ruid);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
            String paperHtml = null;
            try {
                paperHtml = restTemplate.postForObject(configuration.getTestPaperHost() + "/vls2s/vls3isapi.dll/smpaper", entity, String.class);
                final TestPaperResult testPaperResult = parse.parseSubmitTestPaperResult(paperHtml);
                action.accept(testPaperResult);
            } catch (Exception e) {
                log.warn("提交失败失败 [{}, errMessage:{} html: {}] ", paperId, e.getMessage(), paperHtml, e);
            }
        }, delay, TimeUnit.SECONDS);
    }
}
