package com.edupedu.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.edupedu.app.repository.ClassRepository;
import com.edupedu.app.repository.ScheduleRepository;
import com.edupedu.app.repository.SessionRepository;
import com.edupedu.app.repository.StudentGroupRepository;
import com.edupedu.app.request.ScheduleDTO;
import com.edupedu.app.request.CreateScheduleRequest;
import com.edupedu.app.request.GenerateScheduleRequest;
import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.Schedule;
import com.edupedu.app.model.Session;
import com.edupedu.app.model.StudentGroup;
import com.edupedu.app.model.Class;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final ClassRepository classRepository;
    private final SessionRepository sessionRepository;

    public List<ScheduleDTO> getWeeklyScheduleForClass(Long studentGroupId) {
        return scheduleRepository.findByStudentGroupId(studentGroupId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> getWeeklyScheduleForTeacher(Long teacherId) {
        return scheduleRepository.findByClazzTeacherId(teacherId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleDTO createSchedule(CreateScheduleRequest request) {
        
        Class clazz = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", request.getClassId()));

        validateNoConflicts(clazz.getTeacher().getId(), request.getStudentGroupId(),
                request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), null);

        StudentGroup studentGroup = studentGroupRepository.findById(request.getStudentGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("StudentGroup", "id", request.getStudentGroupId()));

        Schedule schedule = Schedule.builder()
                .studentGroup(studentGroup)
                .clazz(clazz)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .room(request.getRoom())
                .lessonNumber(request.getLessonNumber())
                .build();

        schedule = scheduleRepository.save(schedule);
        return mapToDTO(schedule);
    }

    @Transactional
    public List<ScheduleDTO> generateSchedule(GenerateScheduleRequest request) {
        List<Schedule> generatedSchedules = new ArrayList<>();

        LocalTime dayStart = request.getDayStartTime() != null ? request.getDayStartTime() : LocalTime.of(8, 0);
        LocalTime dayEnd = request.getDayEndTime() != null ? request.getDayEndTime() : LocalTime.of(15, 0);
        int lessonDuration = request.getLessonDurationMinutes() != null ? request.getLessonDurationMinutes() : 45;
        int breakDuration = request.getBreakDurationMinutes() != null ? request.getBreakDurationMinutes() : 15;

        DayOfWeek[] weekDays = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY };

        for (GenerateScheduleRequest.ClassMapping mapping : request.getClassMappings()) {
            
            Class clazz = classRepository.findById(mapping.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class", "id", mapping.getClassId()));

            for (Long studentGroupId : mapping.getStudentGroupIds()) {
                StudentGroup studentGroup = studentGroupRepository.findById(studentGroupId)
                        .orElseThrow(() -> new ResourceNotFoundException("StudentGroup", "id", studentGroupId));

                // We calculate lessons needed roughly or through credits
                int lessonsNeeded = clazz.getSubject() != null ? 2 : 1; 
                int lessonsScheduled = 0;

                outerLoop: for (DayOfWeek day : weekDays) {
                    LocalTime currentTime = dayStart;
                    int lessonNumber = 1;

                    while (currentTime.plusMinutes(lessonDuration).isBefore(dayEnd) ||
                            currentTime.plusMinutes(lessonDuration).equals(dayEnd)) {

                        LocalTime endTime = currentTime.plusMinutes(lessonDuration);

                        // Check for conflicts
                        List<Schedule> teacherConflicts = scheduleRepository.findConflictingTeacherSchedules(
                                clazz.getTeacher().getId(), day, currentTime, endTime);
                        List<Schedule> classConflicts = scheduleRepository.findConflictingClassSchedules(
                                studentGroup.getId(), day, currentTime, endTime);

                        if (teacherConflicts.isEmpty() && classConflicts.isEmpty()) {
                            Schedule schedule = Schedule.builder()
                                    .studentGroup(studentGroup)
                                    .clazz(clazz)
                                    .dayOfWeek(day)
                                    .startTime(currentTime)
                                    .endTime(endTime)
                                    .lessonNumber(lessonNumber)
                                    .build();

                            schedule = scheduleRepository.save(schedule);
                            generatedSchedules.add(schedule);
                            lessonsScheduled++;

                            if (lessonsScheduled >= lessonsNeeded) {
                                break outerLoop;
                            }
                        }

                        currentTime = currentTime.plusMinutes(lessonDuration + breakDuration);
                        lessonNumber++;
                    }
                }
            }
        }

        return generatedSchedules.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void generateSessionsForNextWeek() {
        List<Schedule> allSchedules = scheduleRepository.findAll();
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate nextWeekStart = today.with(java.time.temporal.TemporalAdjusters.next(DayOfWeek.MONDAY));
        
        for (Schedule schedule : allSchedules) {
            java.time.LocalDate sessionDate = nextWeekStart.with(java.time.temporal.TemporalAdjusters.nextOrSame(schedule.getDayOfWeek()));
            
            // Check if session already exists
            boolean exists = sessionRepository.findByScheduleId(schedule.getId()).stream()
                    .anyMatch(s -> s.getDate().equals(sessionDate));
            
            if (!exists) {
                Session session = Session.builder()
                        .schedule(schedule)
                        .date(sessionDate)
                        .room(schedule.getRoom())
                        .build();
                sessionRepository.save(session);
            }
        }
    }

    @Transactional
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Schedule", "id", id);
        }
        scheduleRepository.deleteById(id);
    }

    private void validateNoConflicts(Long teacherId, Long studentGroupId, DayOfWeek day,
            LocalTime startTime, LocalTime endTime, Long excludeId) {
        List<Schedule> teacherConflicts = scheduleRepository.findConflictingTeacherSchedules(
                teacherId, day, startTime, endTime);

        if (excludeId != null) {
            teacherConflicts = teacherConflicts.stream()
                    .filter(s -> !s.getId().equals(excludeId))
                    .collect(Collectors.toList());
        }

        if (!teacherConflicts.isEmpty()) {
            throw new IllegalArgumentException("Teacher has a conflicting schedule at this time");
        }

        List<Schedule> classConflicts = scheduleRepository.findConflictingClassSchedules(
                studentGroupId, day, startTime, endTime);

        if (excludeId != null) {
            classConflicts = classConflicts.stream()
                    .filter(s -> !s.getId().equals(excludeId))
                    .collect(Collectors.toList());
        }

        if (!classConflicts.isEmpty()) {
            throw new IllegalArgumentException("Class has a conflicting schedule at this time");
        }
    }

    private ScheduleDTO mapToDTO(Schedule schedule) {
        return ScheduleDTO.builder()
        .id(schedule.getId())
        .studentGroupId(schedule.getStudentGroup().getId())
        .classId(schedule.getClazz().getId())
        .dayOfWeek(schedule.getDayOfWeek())
        .startTime(schedule.getStartTime())
        .endTime(schedule.getEndTime())
        .room(schedule.getRoom())
        .lessonNumber(schedule.getLessonNumber())
        .build();

        // new ScheduleDTO(
        //         schedule.getId(),
        //         schedule.getStudentGroup().getId(),
        //         schedule.getStudentGroup().getName(),
        //         schedule.getClazz().getTeacher().getId(),
        //         schedule.getClazz().getTeacher().getUser().getFirstName() + " " + schedule.getClazz().getTeacher().getUser().getLastName(),
        //         schedule.getClazz().getSubject().getId(),
        //         schedule.getClazz().getSubject().getName(),
        //         schedule.getDayOfWeek(),
        //         schedule.getStartTime(),
        //         schedule.getEndTime(),
        //         schedule.getRoom(),
        //         schedule.getLessonNumber()
        // );

        

    }
}
