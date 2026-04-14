package com.edupedu.app.repository;

import com.edupedu.app.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUniversityId(Long universityId);

    List<Course> findByTeacherId(Long teacherId);

    List<Course> findByIsPublicTrue();

    List<Course> findByIsPublicTrueAndUniversityId(Long universityId);
}