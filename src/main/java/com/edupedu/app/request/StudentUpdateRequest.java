package com.edupedu.app.request;

public record StudentUpdateRequest(
        String studentNumber,

        String accountNumber,

        String parentPhone,

        Long studentGroupId
) {
}
