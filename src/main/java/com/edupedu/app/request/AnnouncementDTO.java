package com.edupedu.app.request;

import java.time.LocalDateTime;

import com.edupedu.app.model.enums.Role;

import lombok.Builder;

@Builder
public record AnnouncementDTO(
    Long id,
    String title,
    String content,
    Long authorId,
    String authorName,
    Role targetRole,
    Long targetStudentGroupId,
    String targetStudentGroupName,
    Boolean important,
    LocalDateTime expiresAt,
    LocalDateTime createdAt
) {
    
}
