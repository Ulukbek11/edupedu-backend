package com.edupedu.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.CourseLessonProgress;

@Repository
public interface CourseLessonProgressRepository extends JpaRepository<CourseLessonProgress, Long> {

    Optional<CourseLessonProgress> findByStudentIdAndCourseLessonId(Long studentId, Long lessonId);

    boolean existsByStudentIdAndCourseLessonId(Long studentId, Long lessonId);

    @Query("SELECT COUNT(lp) FROM CourseLessonProgress lp WHERE lp.student.id = :studentId AND lp.courseLesson.module.course.id = :courseId")
    long countCompletedByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Query("SELECT lp FROM CourseLessonProgress lp WHERE lp.student.id = :studentId AND lp.courseLesson.module.course.id = :courseId")
    List<CourseLessonProgress> findByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}