package com.edupedu.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.CourseTestQuestion;

@Repository
public interface CourseTestQuestionRepository extends JpaRepository<CourseTestQuestion, Long> {
    List<CourseTestQuestion> findByCourseTestIdOrderByOrderIndexAsc(Long courseTestId);
}
