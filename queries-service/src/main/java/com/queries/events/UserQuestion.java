package com.queries.events;

import com.queries.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wanghongen
 * 2019-05-26
 */
@AllArgsConstructor
@Data
public class UserQuestion {
    private String token;
    private User user;
}