package com.common.utils.alarm;

/**
 * @author wanghongen
 * 2018/7/2
 */
public interface Alarm {

    /**
     * 发送报警信息
     *
     * @param receiver 报警接受者
     * @param subject  报警主题
     * @param body     报警内容
     */
    void alarm(String receiver, String subject, String body);

    /**
     * 发送报警信息
     *
     * @param receiver 报警接受者
     * @param subject  报警主题
     * @param e        异常信息
     */
    void alarm(String receiver, String subject, Throwable e);
}
