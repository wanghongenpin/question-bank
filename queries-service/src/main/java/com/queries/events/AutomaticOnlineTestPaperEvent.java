package com.queries.events;

import org.springframework.context.ApplicationEvent;

/**
 * @author wanghongen
 * 2019-05-27
 */
public class AutomaticOnlineTestPaperEvent extends ApplicationEvent {

    public AutomaticOnlineTestPaperEvent(UserQuestion source) {
        super(source);
    }

    @Override
    public UserQuestion getSource() {
        return (UserQuestion) super.getSource();
    }
}
