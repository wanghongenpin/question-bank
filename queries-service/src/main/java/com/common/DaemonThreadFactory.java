package com.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author wanghongen
 * 2019-03-14
 */

/**
 * @author wanghongen
 * 2018/8/2
 */
public class DaemonThreadFactory implements ThreadFactory {
    private final LongAdder threadNumber = new LongAdder();
    private String threadNamePrefix;

    public DaemonThreadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public DaemonThreadFactory() {
    }

    @Override
    public Thread newThread(Runnable r) {
        final Thread thread = new Thread(r);
        thread.setDaemon(true);
        if (threadNamePrefix != null) {
            threadNumber.increment();
            thread.setName(threadNamePrefix + threadNumber.longValue());
        }
        return thread;
    }
}
