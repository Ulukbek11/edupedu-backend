package com.edupedu.app.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateScheduleRequest {
    private LocalTime dayStartTime;
    private LocalTime dayEndTime;
    private Integer lessonDurationMinutes;
    private Integer breakDurationMinutes;
    private List<ClassMapping> classMappings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassMapping {
        private Long classId;
        private List<Long> studentGroupIds;
    }
}
