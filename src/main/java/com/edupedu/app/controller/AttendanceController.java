package com.edupedu.app.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edupedu.app.model.User;
import com.edupedu.app.model.enums.Role;
import com.edupedu.app.repository.StudentRepository;
import com.edupedu.app.request.AttendanceDTO;
import com.edupedu.app.request.MarkAttendanceRequest;
import com.edupedu.app.service.AttendanceService;
import com.edupedu.app.service.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AttendanceController {

        private final ReportService reportService;

        private final AttendanceService attendanceService;

        private final StudentRepository studentRepository;

    @GetMapping("/attendance/reports/attendance")
        public ResponseEntity<Map<String, Object>> getAttendanceStats(
                        @RequestParam(required = false) Long classGroupId,
                        @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
                        @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {
                return ResponseEntity.ok(reportService.getAttendanceStats(classGroupId, startDate, endDate));
        }



         @GetMapping("/attendance")
    public ResponseEntity<List<AttendanceDTO>> getMyAttendance(@AuthenticationPrincipal User user) {
        if (user.getRole() == Role.ROLE_UNIVERSITY_ADMIN || user.getRole() == Role.ROLE_ADMIN) {
            return ResponseEntity.ok(attendanceService.getAllAttendance());
        }
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(attendanceService.getStudentAttendance(student.getId()));
    }

    @GetMapping("/attendance/range")
    public ResponseEntity<List<AttendanceDTO>> getMyAttendanceByDateRange(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (user.getRole() == Role.ROLE_UNIVERSITY_ADMIN || user.getRole() == Role.ROLE_ADMIN) {
            return ResponseEntity.ok(attendanceService.getAttendanceByDateRange(startDate, endDate));
        }

        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity
                .ok(attendanceService.getStudentAttendanceByDateRange(student.getId(), startDate, endDate));
    }

    @GetMapping("/attendance/stats")
    public ResponseEntity<Map<String, Long>> getMyAttendanceStats(@AuthenticationPrincipal User user) {
        if (user.getRole() == Role.ROLE_UNIVERSITY_ADMIN || user.getRole() == Role.ROLE_ADMIN) {
            return ResponseEntity.ok(attendanceService.getAttendanceStats());
        }
        var student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        return ResponseEntity.ok(attendanceService.getStudentAttendanceStats(student.getId()));
    }

    @GetMapping("/attendance/student/{studentId}")
    public ResponseEntity<List<AttendanceDTO>> getStudentAttendance(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(studentId));
    }

    @PostMapping("/teacher/attendance")
    public ResponseEntity<List<AttendanceDTO>> markAttendance(
            @Valid @RequestBody MarkAttendanceRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(attendanceService.markAttendance(request, user.getId()));
    }

    @GetMapping("/teacher/attendance/schedule/{scheduleId}")
    public ResponseEntity<List<AttendanceDTO>> getScheduleAttendance(
            @PathVariable Long scheduleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getScheduleAttendance(scheduleId, date));
    }
}
