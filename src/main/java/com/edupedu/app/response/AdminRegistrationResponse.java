package com.edupedu.app.response;

public record AdminRegistrationResponse<User, UserType>(
    boolean success,
    String message,
    User user,
    UserType userType
) {
    
}
