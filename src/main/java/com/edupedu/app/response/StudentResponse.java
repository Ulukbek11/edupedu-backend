package com.edupedu.app.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StudentResponse(
        @JsonProperty("id")
        Long id,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("email")
        String email,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        @JsonProperty("full_name")
        String fullName,

        @JsonProperty("student_number")
        String studentNumber,

        @JsonProperty("account_number")
        String accountNumber,

        @JsonProperty("parent_phone")
        String parentPhone,

        @JsonProperty("student_group_id")
        Long studentGroupId
) {
}
