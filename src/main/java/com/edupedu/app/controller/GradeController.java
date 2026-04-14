package com.edupedu.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.edupedu.app.model.User;
import com.edupedu.app.model.enums.Role;
import com.edupedu.app.repository.StudentRepository;
import com.edupedu.app.request.CreateGradeRequest;
import com.edupedu.app.request.GradeDTO;
import com.edupedu.app.service.GradeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;
    private final StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<List<GradeDTO>> getMyGrades(@AuthenticationPrincipal User user) {
        if (user.getRole() == Role.ROLE_UNIVERSITY_ADMIN || user.getRole() == Role.ROLE_ADMIN) {
            return ResponseEntity.ok(gradeService.getAllGrades());
        }
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(gradeService.getStudentGrades(student.getId()));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<GradeDTO>> getMyGradesBySubject(
            @AuthenticationPrincipal User user,
            @PathVariable Long subjectId) {
        if (user.getRole() == Role.ROLE_UNIVERSITY_ADMIN || user.getRole() == Role.ROLE_ADMIN) {
            return ResponseEntity.ok(gradeService.getGradesBySubject(subjectId));
        }
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(gradeService.getStudentGradesBySubject(student.getId(), subjectId));
    }

    @GetMapping("/averages")
    public ResponseEntity<Map<String, Double>> getMyGradeAverages(@AuthenticationPrincipal User user) {
        if (user.getRole() == Role.ROLE_UNIVERSITY_ADMIN || user.getRole() == Role.ROLE_ADMIN) {
            return ResponseEntity.ok(gradeService.getGradeAverages());
        }
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(gradeService.getStudentGradeAverages(student.getId()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeDTO>> getStudentGrades(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.getStudentGrades(studentId));
    }

    @PostMapping
    public ResponseEntity<GradeDTO> createGrade(
            @Valid @RequestBody CreateGradeRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(gradeService.createGrade(request, user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}
