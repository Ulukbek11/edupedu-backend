package com.edupedu.app.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TeacherResponse(
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

        @JsonProperty("employee_number")
        String employeeNumber,

        @JsonProperty("subject_ids")
        List<Long> subjectIds,

        @JsonProperty("curator_id")
        Long curatorId
) {
}
