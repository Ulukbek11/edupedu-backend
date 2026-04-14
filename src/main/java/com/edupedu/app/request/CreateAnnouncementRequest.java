package com.edupedu.app.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import com.edupedu.app.model.enums.Role;

@Builder
public record CreateAnnouncementRequest (
    @NotBlank(message = "Title is required")
    String title,
    @NotBlank(message = "Content is required")
    String content,
    Role targetRole, // null for all roles
    Long targetStudentGroupId, // null for all classes
    Boolean important,
    LocalDateTime expiresAt
){

}
