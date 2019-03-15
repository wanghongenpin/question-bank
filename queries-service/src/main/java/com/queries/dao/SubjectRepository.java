package com.queries.dao;

import com.queries.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author wanghongen
 * 2018/5/2
 */
public interface SubjectRepository extends JpaRepository<Subject, String> {
    @Query("select distinct name from Subject")
    List<String> getALlSubjectName();

    Boolean existsSubjectByName(String name);
}
