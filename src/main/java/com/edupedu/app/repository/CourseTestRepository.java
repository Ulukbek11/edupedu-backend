package com.edupedu.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.CourseTest;

@Repository
public interface CourseTestRepository extends JpaRepository<CourseTest, Long> {
    List<CourseTest> findByModuleId(Long moduleId);

    @Query("SELECT COUNT(t) FROM CourseTest t WHERE t.module.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT t FROM CourseTest t WHERE t.module.course.id = :courseId")
    List<CourseTest> findByCourseId(@Param("courseId") Long courseId);
}
