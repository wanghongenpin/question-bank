package com.question.service;

import com.question.model.User;

import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/2
 */
public interface UserService {
    Optional<User> getUser(String username);


    User saveUser(User user);
}
