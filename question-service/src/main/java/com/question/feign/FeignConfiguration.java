package com.question.feign;

import feign.Logger;
import feign.codec.Decoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author wanghongen
 * 2018/5/1
 */
//public class FeignConfiguration {
//    @Resource
//    private GBKDecoder gbkDecoder;
//
//
//    // 开启Feign的日志
//    @Bean
//    public Logger.Level logger() {
//        return Logger.Level.FULL;
//    }
//
//
//    @Bean
//    public Decoder feignDecoder() {
//        return gbkDecoder;
//    }
//
//
//}
