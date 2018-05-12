package com.question.service;

import com.question.model.Subject;

import java.util.List;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/2
 */
public interface SubjectService {
    Optional<Subject> getSubject(String id);

    Subject saveSubject(Subject subject);

    List<String> getALlSubjectName();

    List<Subject> list(String ownerSpecialty);
}
