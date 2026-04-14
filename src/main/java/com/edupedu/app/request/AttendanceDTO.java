package com.edupedu.app.request;


import java.time.LocalDate;
import java.time.LocalDateTime;

import com.edupedu.app.model.enums.AttendanceStatus;

import lombok.Builder;

@Builder
public record AttendanceDTO (
    Long id,
    Long studentId,
    Long scheduleId,
    LocalDate date,
    AttendanceStatus status,
    String notes,
    String markedByName,
    LocalDateTime markedAt
){}

