package com.queries.dao;

import com.queries.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/1
 */
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);
}
