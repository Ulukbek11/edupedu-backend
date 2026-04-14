package com.edupedu.app.request;

import com.edupedu.app.model.enums.Role;
import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
        @Email
        String email,

        String phone,

        String firstName,

        String lastName,

        Role role,

        Long universityId,

        Boolean emailVerified,

        Boolean enabled,

        Boolean locked,

        Boolean expired
) {
}
