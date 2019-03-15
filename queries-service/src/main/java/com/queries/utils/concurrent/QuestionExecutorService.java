package com.queries.utils.concurrent;

import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * @author wanghongen
 * 2018/5/5
 */
@Service
public class QuestionExecutorService {
//    ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());


    public static final ExecutorService executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 3, Runtime.getRuntime().availableProcessors() * 3,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), (ThreadFactory) Thread::new);
}
