package com.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * @author wanghongen
 * 2018/11/9
 */
@RequestMapping("/index")
@RestController
public class IndexController {
    @Value("${name:null}")
    private String name;
    @Resource
    private GatewayProperties properties;
    @Autowired
    private Environment environment;

    @GetMapping
    public Map<String, Long> index() {
        Map<String, Long> time = Collections.singletonMap("time", Instant.now().toEpochMilli());

        return time;
    }
}
