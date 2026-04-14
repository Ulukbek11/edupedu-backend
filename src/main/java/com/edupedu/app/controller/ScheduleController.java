package com.edupedu.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edupedu.app.model.User;
import com.edupedu.app.repository.StudentRepository;
import com.edupedu.app.repository.TeacherRepository;
import com.edupedu.app.request.CreateScheduleRequest;
import com.edupedu.app.request.GenerateScheduleRequest;
import com.edupedu.app.request.ScheduleDTO;
import com.edupedu.app.service.ScheduleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @GetMapping("/week")
    public ResponseEntity<List<ScheduleDTO>> getMyWeeklySchedule(@AuthenticationPrincipal User user) {
        List<ScheduleDTO> schedule;

        switch (user.getRole()) {
            case ROLE_STUDENT -> {
                var student = studentRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new IllegalStateException("Student profile not found"));
                schedule = scheduleService.getWeeklyScheduleForClass(student.getStudentGroup().getId());
            }
            case ROLE_TEACHER -> {
                var teacher = teacherRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new IllegalStateException("Teacher profile not found"));
                schedule = scheduleService.getWeeklyScheduleForTeacher(teacher.getId());
            }
            case ROLE_UNIVERSITY_ADMIN, ROLE_ADMIN -> schedule = scheduleService.getAllSchedules();
            default -> throw new IllegalStateException("Unknown role");
        }

        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/class/{classGroupId}")
    public ResponseEntity<List<ScheduleDTO>> getClassSchedule(@PathVariable Long classGroupId) {
        return ResponseEntity.ok(scheduleService.getWeeklyScheduleForClass(classGroupId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ScheduleDTO>> getTeacherSchedule(@PathVariable Long teacherId) {
        return ResponseEntity.ok(scheduleService.getWeeklyScheduleForTeacher(teacherId));
    }

    @PostMapping
    public ResponseEntity<ScheduleDTO> createSchedule(@Valid @RequestBody CreateScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.createSchedule(request));
    }

    @PostMapping("/generate")
    public ResponseEntity<List<ScheduleDTO>> generateSchedule(@Valid @RequestBody GenerateScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.generateSchedule(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
