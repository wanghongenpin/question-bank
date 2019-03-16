package com.queries.services.impl;

import com.alibaba.fastjson.JSON;
import com.common.utils.Either;
import com.queries.api.ApiService;
import com.queries.dao.UserRepository;
import com.queries.events.UploadQueriesEvent;
import com.queries.exceptions.ApiException;
import com.queries.exceptions.QueriesServiceException;
import com.queries.models.User;
import com.queries.parses.HtmlParse;
import com.queries.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

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
    private HtmlParse parse;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private UserRepository repository;

    @Override
    public Either<ApiException, User> uploadUserQuestionBank(String username, String password) {
        ResponseEntity<String> loginResponse = apiService.login(username, password);
        String body = loginResponse.getBody();
        log.debug("登录返回 username: {}, body: {}", username, body);
        Optional<String> tokenOptional = parse.parseToken(body);
        log.info("解析token  username: {}, token: {}", username, tokenOptional);

        return tokenOptional.map(token ->
                parse.parseUser(apiService.getUserInfo(token).getBody()).map(u -> {
                    log.info("解析用户 user: {}", JSON.toJSONString(u));
                    u.setPassword(password);
                    User user = saveUser(u);

                    try {
                        applicationContext.publishEvent(new UploadQueriesEvent(new UploadQueriesEvent.UserQueries(token, u)));
                    } catch (RejectedExecutionException e) {
                        //线程池拒绝
                        return Either.<ApiException, User>left(QueriesServiceException.RejectedExecutionException.build());
                    }

                    return Either.<ApiException, User>right(user);
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
