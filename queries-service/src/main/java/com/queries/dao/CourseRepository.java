package com.queries.dao;

import com.queries.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author wanghongen
 * 2018/5/2
 */
public interface CourseRepository extends JpaRepository<Course, String> {
    @Query("select distinct name from Course")
    List<String> getAllCourseName();

    Boolean existsCourseByName(String name);
}
