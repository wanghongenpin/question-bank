package com.queries.events;

import com.queries.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @author wanghongen
 * 2019-03-14
 */
public class UploadQueriesEvent extends ApplicationEvent {
    public UploadQueriesEvent(UserQueries userQueries) {
        super(userQueries);
    }

    @Override
    public UserQueries getSource() {
        return (UserQueries) super.getSource();
    }

    @AllArgsConstructor
    @Data
    public static class UserQueries {
        private String token;
        private User user;
    }
}
