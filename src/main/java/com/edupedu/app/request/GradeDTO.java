package com.edupedu.app.request;

import lombok.Builder;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record GradeDTO (
    Long id,
    Long studentId,
    Long takenClassId,
    Double value,
    Double maxValue,
    String gradeType,
    String description,
    LocalDate date,
    LocalDateTime createdAt
){}
