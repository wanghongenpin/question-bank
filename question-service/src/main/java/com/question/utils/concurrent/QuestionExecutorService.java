package com.question.utils.concurrent;

import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * @author wanghongen
 * 2018/5/5
 */
@Service
public class QuestionExecutorService {
//    ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());


    public static final ExecutorService loginExecutorService = Executors.newCachedThreadPool();
    public static final ExecutorService executorService = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() + 2,
            30, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
}
