package com.queries;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * @author wanghongen
 * 2019-03-16
 */
@Configuration
public class RestTemplateConfiguration {
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory();

        factory.setConnectTimeout(5000);
        factory.setReadTimeout(20000);

        //禁用Apache HTTP Client的Cookie
        HttpClient httpClient = HttpClientBuilder.create()
                .disableCookieManagement()
                .useSystemProperties()
                .build();
        factory.setHttpClient(httpClient);
        return factory;
    }

    @Primary
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        // 支持中文编码
        StringHttpMessageConverter messageConverter = new StringHttpMessageConverter(Charset.forName("GBK"));
        RestTemplate restTemplate = new RestTemplateBuilder()
                .requestFactory(() -> clientHttpRequestFactory)
                .errorHandler(NoOpResponseErrorHandler.INSTANCE)
                .build();
        restTemplate.getMessageConverters().set(0, messageConverter);
        return restTemplate;
    }

    public enum NoOpResponseErrorHandler implements ResponseErrorHandler {
        INSTANCE;

        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) {
            //返回false表示不管response的status是多少都返回没有错
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) {
            //处理rest异常
        }
    }
}
