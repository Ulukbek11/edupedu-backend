package com.edupedu.app.repository;

import com.edupedu.app.model.CourseLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseLessonRepository extends JpaRepository<CourseLesson, Long> {
    List<CourseLesson> findByModuleIdOrderByOrderIndexAsc(Long moduleId);

    int countByModuleId(Long moduleId);

    @Query("SELECT COUNT(l) FROM CourseLesson l WHERE l.module.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT l FROM CourseLesson l WHERE l.module.course.id = :courseId")
    List<CourseLesson> findByCourseId(@Param("courseId") Long courseId);
}
