package com.edupedu.app.request;

import java.util.List;

public record TeacherUpdateRequest(
        String employeeNumber,

        List<Long> subjectIds
) {
}
