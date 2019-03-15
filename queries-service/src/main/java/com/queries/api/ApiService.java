package com.queries.api;

import com.common.utils.MD5;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.Charset;

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

    @Bean
    public RestTemplate restTemplate() {
        StringHttpMessageConverter messageConverter = new StringHttpMessageConverter(Charset.forName("GBK"));
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.getMessageConverters().set(0, messageConverter); // 支持中文编码
        return restTemplate;
    }

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

    public String subjectBankLogin(String token) {
        HttpHeaders requestHeaders = new HttpHeaders();
        String md5 = MD5.md5(token);
        String cookie = "zdyj2web=" + md5;
        requestHeaders.set("Cookie", cookie);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, requestHeaders);
        restTemplate.exchange(configuration.getSubjectBankLoginUrl(), HttpMethod.GET, requestEntity, String.class, token);
        return cookie;
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
