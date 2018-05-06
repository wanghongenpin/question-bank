package com.question.api;

import com.question.utils.cache.CacheService;
import org.apache.commons.codec.digest.Md5Crypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Collections;

/**
 * @author wanghongen
 * 2018/5/3
 */
@Service
public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ApiConfiguration configuration;
    @Resource
    private CacheService cacheService;

    @Bean
    public RestTemplate restTemplate() {
        StringHttpMessageConverter messageConverter = new StringHttpMessageConverter(Charset.forName("GBK"));
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.getMessageConverters().set(0, messageConverter); // 支持中文编码

        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.add(HttpHeaders.COOKIE, cacheService.get());
            return execution.execute(request, body);
        }));
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
        String md5 = Md5Crypt.md5Crypt(token.getBytes());
        requestHeaders.set("Cookie", "zdyj2web=" + md5);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, requestHeaders);
        restTemplate.exchange(configuration.getSubjectBankLoginUrl(), HttpMethod.GET, requestEntity, String.class, token);
        return md5;
    }

    public ResponseEntity<String> getSubjects() {
        return restTemplate.getForEntity(configuration.getSubjectsUrl(), String.class);
    }

    public ResponseEntity<String> getSubject(String id) {
        return restTemplate.getForEntity(configuration.getSubjectUrl(), String.class, id);
    }


    public ResponseEntity<String> getQuestions() {
        return restTemplate.getForEntity(configuration.getQuestionBankUrl(), String.class);
    }


    public ResponseEntity<String> getQuestion(String id) {
        return restTemplate.getForEntity(configuration.getQuestionUrl(), String.class, id);
    }
}
