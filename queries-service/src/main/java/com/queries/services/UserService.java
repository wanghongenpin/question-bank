package com.queries.services;

import com.common.utils.Either;
import com.queries.exceptions.ApiException;
import com.queries.models.User;

import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/2
 */
public interface UserService {
    /**
     * 上传用户题库
     *  @param username 用户名
     * @param password 密码
     */
    Either<ApiException, User> uploadUserQuestionBank(String username, String password);

    Optional<User> getUser(String username);


    User saveUser(User user);
}
