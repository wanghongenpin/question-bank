package com.queries.utils.concurrent;

import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wanghongen
 * 2018/5/5
 */
@Service
public class QuestionExecutorService {
//    ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());


    public static final ExecutorService executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, Runtime.getRuntime().availableProcessors() * 3,
            0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
}
