package com.question.service;

import com.question.model.Subject;

import java.util.List;

/**
 * @author wanghongen
 * 2018/5/2
 */
public interface SubjectService {
    Subject saveSubject(Subject subject);

    List<String> getALlSubjectName();
}
