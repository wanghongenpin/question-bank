package com.common.utils.alarm;

import com.common.DaemonThreadFactory;
import com.common.utils.IpUtils;
import com.common.utils.QuestionStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wanghongen
 * 2018/7/2
 */
public abstract class BaseAlarm implements Alarm {

    protected static final Long DEFAULT_INTERVAL = 300L; // 发送报警默认间隔时间，300秒
    private static final long CLEAR_INTERVAL = 720 * 60 * 1000; // 12个小时内没有新增报警，则清除相应的key
    private final ConcurrentMap<String, Long> sendRecord = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, AlarmContent> alarmMap = new ConcurrentHashMap<>();

    public BaseAlarm() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new DaemonThreadFactory());
        executor.scheduleWithFixedDelay(new AlarmThread(), getIntervalTime(), getIntervalTime(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void alarm(String receiver, String subject, String body) {
        if (validReceiver(receiver)) {
            cacheAlarm(receiver, subject, body, subject);
        }
    }

    @Override
    public void alarm(String receiver, String subject, Throwable e) {
        if (validReceiver(receiver)) {
            cacheAlarm(receiver, subject, ExceptionUtils.getStackTrace(e), subject + e.getClass().getName());
        }
    }

    protected boolean validReceiver(String receiver) {
        return StringUtils.isNotBlank(receiver);
    }

    protected final void cacheAlarm(String receiver, String subject, String body, final String key) {
        Long lastTime = sendRecord.get(key);
        long currentTime = System.currentTimeMillis();
        if (lastTime == null || currentTime - lastTime >= getIntervalTime()) {
            sendAlarm(receiver, subject, IpUtils.getIpWithBracketWrap() + body);
            sendRecord.put(key, currentTime);
        } else {
            AlarmContent ac = alarmMap.get(key);
            if (ac == null) {
                ac = new AlarmContent();
                ac.setBody(body);
                ac.setCount(new AtomicLong());
                ac.setReceiver(receiver);
                ac.setSubject(subject);
                alarmMap.put(key, ac);
            }
            ac.getCount().incrementAndGet();
        }
    }

    protected abstract void sendAlarm(String receiver, String subject, String body);

    protected abstract long getIntervalTime();

    class AlarmThread implements Runnable {

        @Override
        public void run() {
            alarmMap.keySet().forEach(key -> {
                AlarmContent ac = alarmMap.get(key);
                if (ac.getCount().get() == 0
                        && System.currentTimeMillis() - sendRecord.get(key) >= CLEAR_INTERVAL) {
                    alarmMap.remove(key);
                    sendRecord.remove(key);
                } else if (ac.getCount().get() > 0) {
                    sendAlarm(ac.getReceiver(),
                            ac.getSubject()
                                    + QuestionStringUtils.wrapStringWithBracket(String.valueOf(ac.getCount().get())),
                            IpUtils.getIpWithBracketWrap() + ac.getBody());
                    ac.resetCount();
                    sendRecord.put(key, System.currentTimeMillis());
                }
            });

        }

    }
}
