package com.edupedu.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edupedu.app.model.User;
import com.edupedu.app.service.ProgressService;
import com.edupedu.app.service.ProgressService.CourseProgressDTO;
import com.edupedu.app.service.ProgressService.StudentProgressDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/progress/lessons/{lessonId}/complete")
    public ResponseEntity<Void> markLessonComplete(@PathVariable Long lessonId,
            @AuthenticationPrincipal User user) {
        progressService.markLessonComplete(user.getId(), lessonId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/progress/courses/{courseId}")
    public ResponseEntity<CourseProgressDTO> getMyCourseProgress(@PathVariable Long courseId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(progressService.getCourseProgress(user.getId(), courseId));
    }

    @GetMapping("/teacher/progress/courses/{courseId}/students")
    public ResponseEntity<List<StudentProgressDTO>> getStudentProgress(@PathVariable Long courseId) {
        return ResponseEntity.ok(progressService.getStudentProgressForCourse(courseId));
    }
}
