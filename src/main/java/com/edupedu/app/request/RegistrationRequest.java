package com.edupedu.app.request;

import com.edupedu.app.model.enums.Role;

public record RegistrationRequest(String email,
                                String password,
                                String firstName,
                                String lastName,
                                Role role) {

}
