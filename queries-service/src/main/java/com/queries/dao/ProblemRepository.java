package com.queries.dao;

import com.queries.models.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wanghongen
 * 2019-05-22
 */
@Repository
public interface ProblemRepository extends JpaRepository<Problem, String> {
}
