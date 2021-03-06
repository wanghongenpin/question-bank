package com.queries.events;

import org.springframework.context.ApplicationEvent;

/**
 * @author wanghongen
 * 2019-03-14
 */
public class UploadQueriesEvent extends ApplicationEvent {
    public UploadQueriesEvent(UserQuestion userQuestion) {
        super(userQuestion);
    }

    @Override
    public UserQuestion getSource() {
        return (UserQuestion) super.getSource();
    }


}
