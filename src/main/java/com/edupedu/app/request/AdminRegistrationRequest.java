package com.edupedu.app.request;

import java.util.Set;

import com.edupedu.app.model.Subject;
import com.edupedu.app.model.University;
import com.edupedu.app.model.enums.Role;

public record AdminRegistrationRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber,
        Role role,
        University university,
        Long studentGroupId,
        Set<Subject> subjects
        ) {

}
