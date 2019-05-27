package com.queries.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wanghongen
 * 2018/5/3
 */
@Data
@Component
@ConfigurationProperties(prefix = "api.urls")
public class ApiConfiguration {
    private String host;
    private String homepageHost;
    private String testPaperHost;
    private String loginUrl;
    private String userInfoUrl;
    private String questionBankLoginUrl;
    private String coursesUrl;
    private String courseQuestionsUrl;
    private String questionUrl;
    private String testPaperUrl;
}
