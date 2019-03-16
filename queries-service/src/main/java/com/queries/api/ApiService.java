package com.queries.api;

import com.common.utils.Either;
import com.common.utils.Try;
import com.queries.exceptions.ApiException;
import com.queries.exceptions.QueriesServiceException;
import com.queries.exceptions.RestApiException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author wanghongen
 * 2018/5/3
 */
@Service
public class ApiService {
    //    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ApiConfiguration configuration;


    public ResponseEntity<String> login(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("uid", username);
        param.add("pw", password);
        param.add("B1", "+%B5%C7%C2%BC+");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        return restTemplate.postForEntity(configuration.getLoginUrl(), request, String.class);
    }

    public ResponseEntity<String> getUserInfo(String token) {

        return restTemplate.getForEntity(configuration.getUserInfoUrl(), String.class, token);
    }

    /**
     * 使用cookie来实现登陆的
     * 一个token对应cookie
     */
    public Either<ApiException, String> subjectBankLogin(String token) {

        return Try.apply(() -> {

            URL url = URI.create(configuration.getSubjectBankLoginUrl() + token).toURL();
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

    public ResponseEntity<String> getSubjects(String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(configuration.getSubjectsUrl(), HttpMethod.GET, entity, String.class);

    }

    public ResponseEntity<String> getSubjectQuestions(String id, String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        HttpEntity entity = new HttpEntity(headers);

        return restTemplate.exchange(configuration.getSubjectQuestionsUrl(), HttpMethod.GET, entity, String.class, id);
    }


    public ResponseEntity<String> getQuestion(String id, String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(configuration.getQuestionUrl(), HttpMethod.GET, entity, String.class, id);
    }
}
