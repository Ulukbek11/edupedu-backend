package com.edupedu.app.request;

public record StudentDTO(Long id, Long userId, String name, String email, Long stuGroupId,
                        String studentGroupName, String studentNumber, String accountNumber) {
        }