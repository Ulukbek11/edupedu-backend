package com.edupedu.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.CourseTestQuestionChoice;

@Repository
public interface CourseTestQuestionChoiceRepository extends JpaRepository<CourseTestQuestionChoice, Long> {
    List<CourseTestQuestionChoice> findByCourseTestQuestionId(Long questionId);
}
