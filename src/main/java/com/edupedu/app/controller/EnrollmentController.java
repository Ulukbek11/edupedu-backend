package com.edupedu.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edupedu.app.model.CourseEnrollment;
import com.edupedu.app.model.User;
import com.edupedu.app.service.CourseEnrollmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final CourseEnrollmentService courseEnrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentDTO> enroll(@AuthenticationPrincipal User user,
            @RequestBody EnrollRequest request) {
        CourseEnrollment enrollment = courseEnrollmentService.enroll(user.getId(), request.courseId(), request.password());
        return ResponseEntity.ok(toDTO(enrollment));
    }

    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentDTO>> myEnrollments(@AuthenticationPrincipal User user) {
        List<EnrollmentDTO> enrollments = courseEnrollmentService.getMyEnrollments(user.getId()).stream()
                .map(this::toDTO).toList();
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentDTO>> courseEnrollments(@PathVariable Long courseId) {
        List<EnrollmentDTO> enrollments = courseEnrollmentService.getCourseEnrollments(courseId).stream()
                .map(this::toDTO).toList();
        return ResponseEntity.ok(enrollments);
    }

    private EnrollmentDTO toDTO(CourseEnrollment e) {
        return new EnrollmentDTO(
                e.getId(),
                e.getStudent().getId(),
                e.getStudent().getUser().getFullName(),
                e.getCourse().getId(),
                e.getCourse().getTitle(),
                e.getEnrolledAt().toString());
    }

    public record EnrollRequest(Long courseId, String password) {
    }

    public record EnrollmentDTO(Long id, Long studentId, String studentName,
            Long courseId, String courseTitle, String enrolledAt) {
    }
}
