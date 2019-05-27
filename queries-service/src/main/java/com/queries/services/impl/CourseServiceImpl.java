package com.queries.services.impl;

import com.queries.dao.CourseRepository;
import com.queries.models.Course;
import com.queries.models.User;
import com.queries.services.CourseService;
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
public class CourseServiceImpl implements CourseService {
    @Resource
    private CourseRepository courseRepository;

    @Override
    public Optional<Course> getCourse(String id) {
        return courseRepository.findById(id);
    }

    @Override
    public Course saveCourse(Course course, User user) {
        course.setCreatedUsername(user.getUsername());
        course.setOwnerSpecialty(user.getSpecialty());

        LocalDateTime now = LocalDateTime.now();
        course.setCreatedTime(now);
        course.setUpdatedTime(now);
        return courseRepository.save(course);
    }

    @Override
    public boolean containsCourse(String CourseName) {
        return courseRepository.existsCourseByName(CourseName);
    }

    @Override
    public List<String> getAllCourseName() {
        return courseRepository.getAllCourseName();
    }

    @Override
    public List<Course> list(String ownerSpecialty) {
        Example<Course> example = Example.of(Course.builder().ownerSpecialty(ownerSpecialty).build());
        return courseRepository.findAll(example);
    }

}
