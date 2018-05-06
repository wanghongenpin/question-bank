package com.question.service.impl;

import com.question.dao.UserRepository;
import com.question.model.User;
import com.question.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/2
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserRepository repository;

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
