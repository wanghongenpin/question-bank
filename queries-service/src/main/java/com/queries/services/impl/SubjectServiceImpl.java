package com.queries.services.impl;

import com.queries.dao.SubjectRepository;
import com.queries.models.Subject;
import com.queries.models.User;
import com.queries.services.SubjectService;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/2
 */
@Service
public class SubjectServiceImpl implements SubjectService {
    @Resource
    private SubjectRepository subjectRepository;

    @Override
    public Optional<Subject> getSubject(String id) {
        return subjectRepository.findById(id);
    }

    @Override
    public Subject saveSubject(Subject subject, User user) {
        subject.setCreatedUsername(user.getUsername());
        subject.setOwnerSpecialty(user.getSpecialty());

        LocalDateTime now = LocalDateTime.now();
        subject.setCreatedTime(now);
        subject.setUpdatedTime(now);
        return subjectRepository.save(subject);
    }

    @Override
    public boolean containsSubject(String subjectName) {
        return subjectRepository.existsSubjectByName(subjectName);
    }

    @Override
    public List<String> getALlSubjectName() {
        return subjectRepository.getALlSubjectName();
    }

    @Override
    public List<Subject> list(String ownerSpecialty) {
        Example<Subject> example = Example.of(Subject.builder().ownerSpecialty(ownerSpecialty).build());
        return subjectRepository.findAll(example);
    }

}
