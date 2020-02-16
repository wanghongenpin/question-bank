package com.queries.dao;

import com.queries.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/4
 */
public interface QuestionRepository extends JpaRepository<Question, String> {
    @Query("select distinct typeDescribe from Question")
    List<String> getAllQuestionType();

    Optional<Question> findFirstByQuestionContains(String question);


}
