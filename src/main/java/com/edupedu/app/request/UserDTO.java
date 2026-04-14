package com.edupedu.app.request;
import java.time.LocalDateTime;

import com.edupedu.app.model.enums.Role;


public record UserDTO(
    Long id,
    String email,
    String phone,
    String firstName,
    String lastName,
    Role role,
    boolean emailVerified,

    boolean enabled,

    boolean locked,

    boolean expired,
    LocalDateTime createdAt,

    LocalDateTime lastModifiedAt
) {
} 
