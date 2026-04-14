package com.edupedu.app.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
    @NotBlank(message = "VALIDATION_AUTH_EMAIL_BLANK")
    @Email(message = "VALIDATION_AUTH_EMAIL_FORMAT")
    String email,

    @NotBlank(message = "VALIDATION_AUTH_PASSWORD_BLANK")
    String password
) {
}
