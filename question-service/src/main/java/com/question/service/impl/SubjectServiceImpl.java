package com.question.service.impl;

import com.question.dao.SubjectRepository;
import com.question.model.Subject;
import com.question.service.SubjectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wanghongen
 * 2018/5/2
 */
@Service
public class SubjectServiceImpl implements SubjectService {
    @Resource
    private SubjectRepository subjectRepository;

    @Override
    public Subject saveSubject(Subject subject) {
        LocalDateTime now = LocalDateTime.now();
        subject.setCreatedTime(now);
        subject.setUpdatedTime(now);
        return subjectRepository.save(subject);
    }

    @Override
    public List<String> getALlSubjectName() {
        return subjectRepository.getALlSubjectName();
    }

}
