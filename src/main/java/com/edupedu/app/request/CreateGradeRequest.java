package com.edupedu.app.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateGradeRequest(
    @NotNull(message = "Student ID is required")
    Long studentId,

    @NotNull(message = "Taken class ID is required")
    Long takenClassId,

    @NotNull(message = "Grade value is required")
    Double value,

    Double maxValue,
    String gradeType,
    String description,

    @NotNull(message = "Date is required")
    LocalDate date
) {
}
