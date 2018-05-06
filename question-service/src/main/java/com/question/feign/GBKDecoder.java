package com.question.feign;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * @author wanghongen
 * 2018/5/1
 */
@Component
public class GBKDecoder implements Decoder {
    private static final String GBK = "GBK";

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().asInputStream(), GBK))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();

        }
    }
}