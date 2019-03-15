package com.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wanghongen
 * 2018/11/12
 */
@Data
@Component
@ConfigurationProperties("gateway")
public class GatewayProperties {
    private String test;
}
