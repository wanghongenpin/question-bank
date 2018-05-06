package com.question.dao;

import com.question.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author wanghongen
 * 2018/5/4
 */
public interface QuestionRepository extends JpaRepository<Question, String> {
    Page<Question> findByTitleContaining(String title, Pageable pageable);

    @Query("select distinct typeDescribe from Question")
    List<String> getAllQuestionType();


}
