package com.queries.services;

import com.queries.models.Subject;
import com.queries.models.User;

import java.util.List;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/2
 */
public interface SubjectService {
    Optional<Subject> getSubject(String id);

    /**
     * 保存学科
     *
     * @param subject 学科
     * @param user    所属用户
     */
    Subject saveSubject(Subject subject, User user);

    /**
     * 是否包含学科
     *
     * @param subjectName 学科名字
     */
    boolean containsSubject(String subjectName);


    List<String> getALlSubjectName();

    List<Subject> list(String ownerSpecialty);
}
