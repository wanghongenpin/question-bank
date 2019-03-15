package com.queries.dao;

import com.queries.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author wanghongen
 * 2018/5/5
 */
public interface AnswerRepository extends JpaRepository<Answer, String> {
}
