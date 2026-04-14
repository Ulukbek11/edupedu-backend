package com.edupedu.app.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TeacherCreateRequest(
        @NotNull
        Long userId,

        String employeeNumber,

        List<Long> subjectIds
) {
}
