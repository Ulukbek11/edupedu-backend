package com.edupedu.app.response;

import com.edupedu.app.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long id,

        @JsonProperty("email")
        String email,

        @JsonProperty("phone")
        String phone,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        @JsonProperty("full_name")
        String fullName,

        @JsonProperty("role")
        Role role,

        @JsonProperty("university_id")
        Long universityId,

        @JsonProperty("email_verified")
        boolean emailVerified,

        @JsonProperty("enabled")
        boolean enabled,

        @JsonProperty("locked")
        boolean locked,

        @JsonProperty("expired")
        boolean expired,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("last_modified_at")
        LocalDateTime lastModifiedAt
) {
}
