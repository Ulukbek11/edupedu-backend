package com.edupedu.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.CourseTestAttempt;

@Repository
public interface CourseTestAttemptRepository extends JpaRepository<CourseTestAttempt, Long> {
    List<CourseTestAttempt> findByStudentIdAndCourseTestId(Long studentId, Long courseTestId);

    List<CourseTestAttempt> findByStudentId(Long studentId);

    @Query("SELECT ta FROM CourseTestAttempt ta WHERE ta.student.id = :studentId AND ta.courseTest.module.course.id = :courseId AND ta.submittedAt IS NOT NULL")
    List<CourseTestAttempt> findSubmittedByStudentAndCourse(@Param("studentId") Long studentId,
            @Param("courseId") Long courseId);

    @Query("SELECT ta FROM CourseTestAttempt ta WHERE ta.courseTest.module.course.id = :courseId AND ta.submittedAt IS NOT NULL")
    List<CourseTestAttempt> findSubmittedByCourse(@Param("courseId") Long courseId);
}
