package com.queries;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.queries", "com.common"})
public class QueriesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueriesServiceApplication.class, args);
    }

    /**
     * 自定义异步线程池
     */
    @Bean
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("QueriesAsyncTaskExecutor-");
        executor.setMaxPoolSize(Math.max(Runtime.getRuntime().availableProcessors() * 2, 6));
        executor.setCorePoolSize(Math.max(Runtime.getRuntime().availableProcessors() * 2, 3));
        executor.setQueueCapacity(32);
        return executor;
    }
}

