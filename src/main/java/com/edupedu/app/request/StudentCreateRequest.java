package com.edupedu.app.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentCreateRequest(
        @NotNull
        Long userId,

        @NotBlank
        String studentNumber,

        @NotBlank
        String accountNumber,

        String parentPhone,

        Long studentGroupId
) {
}
