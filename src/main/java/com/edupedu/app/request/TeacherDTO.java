package com.edupedu.app.request;

import java.util.List;

public record TeacherDTO(Long id, Long userId, String name, String email, List<String> subjects,
                        String employeeNumber) {
        }