package com.common.utils.alarm;

import com.common.utils.QuestionStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author wanghongen
 * 2018/7/2
 */
@Component
public class EmailAlarm extends BaseAlarm {
    private static Logger logger = LoggerFactory.getLogger(EmailAlarm.class);

    @Value("${alarm.email.interval.time:300}")
    private Long intervalTime;
    @Value("${alarm.email.receiver:null}")
    private String receiver;
    @Value("${spring.application.name:null}")
    private String applicationName;
    @Resource
    private JavaMailSenderImpl mailSender;

    @Override
    public void alarm(String receiver, String subject, Throwable e) {
        if (validReceiver(receiver)) {
            String body = ExceptionUtils.getStackTrace(e);
            cacheAlarm(receiver, subject, body, e.getClass().getName());
        }
    }

    public void alarm(String subject, Throwable e) {
        alarm(receiver, subject, e);
    }

    public void alarm(String subject, String body) {
        alarm(receiver, subject, body);
    }

    public void alarm(Throwable e) {
        alarm("", e);
    }

    @Override
    protected void sendAlarm(String receiver, String subject, String body) {
        boolean sendSuccess = false;
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(receiver.split(","));

            message.setSubject(QuestionStringUtils.wrapStringWithBracket(applicationName) + subject);
            message.setText(body);
            message.setFrom(Objects.requireNonNull(mailSender.getUsername()));
            mailSender.send(message);
            sendSuccess = true;
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        } finally {
            logger.info("发送报警邮件 {} receiver:{};subject:{}", sendSuccess, receiver, subject);
        }
    }


    @Override
    protected long getIntervalTime() {
        return (intervalTime == null ? DEFAULT_INTERVAL : intervalTime) * 1000;
    }

}
