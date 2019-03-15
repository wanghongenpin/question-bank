package com.queries.services.impl;

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
    public Either<ApiException, String> uploadUserQuestionBank(String username, String password) {
        ResponseEntity<String> loginResponse = apiService.login(username, password);
        String body = loginResponse.getBody();
        log.info("登录返回 username: {}, body: {}", username, body);
        Optional<String> tokenOptional = parse.parseToken(body);
        log.info("解析token  username: {}, token: {}", username, tokenOptional);
        return tokenOptional.map(token -> {
            try {
                applicationContext.publishEvent(new UploadQueriesEvent(new UploadQueriesEvent.UserQueries(token, username, password)));
            } catch (RejectedExecutionException e) {
                //线程池拒绝
                return Either.<ApiException, String>left(QueriesServiceException.RejectedExecutionException.build());
            }
            return Either.<ApiException, String>right(token);
        }).orElse(Either.left(QueriesServiceException.IllegalUserException.build()));
    }

    @Override
    public Optional<User> getUser(String username) {

        return repository.findById(username);
    }

    @Override
    public User saveUser(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedTime(now);
        user.setUpdatedTime(now);
        return repository.save(user);
    }
}
