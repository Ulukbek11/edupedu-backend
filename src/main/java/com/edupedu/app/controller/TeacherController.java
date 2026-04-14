package com.edupedu.app.controller;

import com.edupedu.app.service.ReportService;
import com.edupedu.app.service.TeacherService;
import com.edupedu.app.service.UserService;
import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.Teacher;
import com.edupedu.app.model.User;
import com.edupedu.app.request.TeacherCreateRequest;
import com.edupedu.app.request.TeacherDTO;
import com.edupedu.app.request.TeacherUpdateRequest;
import com.edupedu.app.response.TeacherResponse;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    private final ReportService reportService;

    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        return new ResponseEntity<>(teacherService.getAllTeachers(), HttpStatus.OK);
    }

    @GetMapping("/university/{universityId}/teachers")
    public ResponseEntity<List<Teacher>> getAllTeachersFromUniversity(@PathVariable Long universityId) {
        return new ResponseEntity<>(teacherService.getAllTeachersFromUniversity(universityId), HttpStatus.OK);
    }

    @GetMapping("/admin/teachers/{id}")
    public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable Long id) {
        return new ResponseEntity<>(teacherService.getTeacherById(id), HttpStatus.OK);
    }

    @GetMapping("/admin/teachers/user/{userId}")
    public ResponseEntity<TeacherResponse> getTeacherByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(teacherService.getTeacherByUserId(userId), HttpStatus.OK);
    }

    @PostMapping("/admin/teachers")
    public ResponseEntity<TeacherResponse> createTeacher(@RequestBody @Valid TeacherCreateRequest request) {
        return new ResponseEntity<>(teacherService.createTeacher(request), HttpStatus.CREATED);
    }

    @PutMapping("/admin/teachers/{id}")
    public ResponseEntity<TeacherResponse> updateTeacher(@PathVariable Long id,
                                                          @RequestBody @Valid TeacherUpdateRequest request) {
        return new ResponseEntity<>(teacherService.updateTeacher(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/admin/teachers/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/teacher/teachers/{teacherId}/subjects")
        @Transactional
        public ResponseEntity<TeacherDTO> updateTeacherSubjects(
                        @PathVariable Long teacherId,
                        @RequestBody List<Long> subjectIds) {
            return new ResponseEntity<>(teacherService.updateTeacherSubjects(teacherId, subjectIds), HttpStatus.OK);
        }

    @GetMapping("/admin/teachers/getWorkload")
    public ResponseEntity<Map<String, Object>> getTeacherWorkload(@RequestParam Long teacherId, @RequestParam int year, @RequestParam int month) {
        return new ResponseEntity<>(reportService.getTeacherWorkload(teacherId, year, month), HttpStatus.OK);
    }
    
}
