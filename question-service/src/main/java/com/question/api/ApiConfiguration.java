package com.question.api;

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
    private String loginUrl;
    private String userInfoUrl;
    private String subjectBankLoginUrl;
    private String subjectsUrl;
    private String subjectUrl;
    private String questionUrl;
}
