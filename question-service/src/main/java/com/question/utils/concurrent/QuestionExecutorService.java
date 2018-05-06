package com.question.utils.concurrent;

import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * @author wanghongen
 * 2018/5/5
 */
@Service
public class QuestionExecutorService {
    public static final ExecutorService executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 6,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), (ThreadFactory) Thread::new);
}
