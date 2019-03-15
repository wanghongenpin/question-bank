package com.queries.services;

import com.queries.models.Subject;

import java.util.List;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/2
 */
public interface SubjectService {
    Optional<Subject> getSubject(String id);

    Subject saveSubject(Subject subject);

    /**
     * 是否包含学科
     *
     * @param subjectName 学科名字
     */
    boolean containsSubject(String subjectName);


    List<String> getALlSubjectName();

    List<Subject> list(String ownerSpecialty);
}
