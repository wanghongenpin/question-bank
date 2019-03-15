package com.common.utils.alarm;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wanghongen
 * 2018/7/2
 */
@Data
public class AlarmContent {
    private String receiver;
    private String subject;
    private String body;
    private AtomicLong count;

    public void resetCount() {
        this.count.set(0);
    }
}
