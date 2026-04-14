package com.edupedu.app.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.DayOfWeek;
import java.time.LocalTime;


@Builder
public record ScheduleDTO(
    Long id,
    Long studentGroupId,
    Long classId,
    DayOfWeek dayOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    String room,
    Integer lessonNumber
) {
}
