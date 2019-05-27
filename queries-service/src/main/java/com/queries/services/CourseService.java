package com.queries.services;

import com.queries.models.Course;
import com.queries.models.User;

import java.util.List;
import java.util.Optional;

/**
 * @author wanghongen
 * 2018/5/2
 */
public interface CourseService {
    Optional<Course> getCourse(String id);

    /**
     * 保存课程
     *
     * @param course 课程
     * @param user   所属用户
     */
    Course saveCourse(Course course, User user);

    /**
     * 是否包含课程
     *
     * @param courseName 学课程名字
     */
    boolean containsCourse(String courseName);


    List<String> getAllCourseName();

    List<Course> list(String ownerSpecialty);
}
