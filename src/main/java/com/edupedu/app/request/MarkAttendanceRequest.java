package com.edupedu.app.request;

import java.time.LocalDate;
import java.util.List;

import com.edupedu.app.model.enums.AttendanceStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MarkAttendanceRequest(
    @NotNull(message = "Schedule ID is required")
    Long scheduleId,
    
    @NotNull(message = "Date is required")
    LocalDate date,

    @NotNull(message = "Attendance records are required")
    List<StudentAttendance> attendanceRecords
) {
    public record StudentAttendance (
        @NotNull
        Long studentId,
        @NotNull
        AttendanceStatus status,
        String notes
    ) {}
}
