package com.queries.services.impl;

import com.alibaba.fastjson.JSON;
import com.common.utils.Either;
import com.queries.api.ApiService;
import com.queries.dao.UserRepository;
import com.queries.events.UserQuestion;
import com.queries.exceptions.ApiException;
import com.queries.exceptions.QueriesServiceException;
import com.queries.models.User;
import com.queries.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/2
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private ApiService apiService;

    @Resource
    private UserRepository repository;

    @Override
    public Either<ApiException, UserQuestion> login(String username, String password) {
        Optional<String> tokenOptional = apiService.login(username, password);
        log.info("解析token  username: {}, token: {}", username, tokenOptional);
        return tokenOptional.map(token ->
                apiService.getUserInfo(token).map(u -> {
                    log.info("解析用户 user: {}", JSON.toJSONString(u));
                    u.setPassword(password);
                    User user = saveUser(u);
                    return Either.<ApiException, UserQuestion>right(new UserQuestion(token, user));
                }).orElseGet(() -> {
                    log.warn("解析用户失败 [token: {}, username: {}, password: {}]", token, username, password);
                    return Either.left(QueriesServiceException.IllegalTokenException.build());
                })
        ).orElse(Either.left(QueriesServiceException.IllegalUserException.build()));
    }

    @Override
    public Optional<User> getUser(String username) {

        return repository.findByUsername(username);
    }

    @Override
    public User saveUser(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setUpdatedTime(now);
        user.setCreatedTime(now);
        getUser(user.getUsername()).ifPresent(u -> user.setId(u.getId()));
        return repository.save(user);
    }
}
