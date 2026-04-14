package com.edupedu.app.repository;

import com.edupedu.app.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByStudentGroupId(Long studentGroupId);

    List<Schedule> findByClazzTeacherId(Long teacherId);

    @Query("SELECT s FROM Schedule s WHERE s.clazz.teacher.id = :teacherId AND s.dayOfWeek = :day " +
           "AND ((s.startTime <= :startTime AND s.endTime > :startTime) " +
           "OR (s.startTime < :endTime AND s.endTime >= :endTime) " +
           "OR (s.startTime >= :startTime AND s.endTime <= :endTime))")
    List<Schedule> findConflictingTeacherSchedules(
            @Param("teacherId") Long teacherId, 
            @Param("day") DayOfWeek day, 
            @Param("startTime") LocalTime startTime, 
            @Param("endTime") LocalTime endTime);

    @Query("SELECT s FROM Schedule s WHERE s.studentGroup.id = :studentGroupId AND s.dayOfWeek = :day " +
           "AND ((s.startTime <= :startTime AND s.endTime > :startTime) " +
           "OR (s.startTime < :endTime AND s.endTime >= :endTime) " +
           "OR (s.startTime >= :startTime AND s.endTime <= :endTime))")
    List<Schedule> findConflictingClassSchedules(
            @Param("studentGroupId") Long studentGroupId, 
            @Param("day") DayOfWeek day, 
            @Param("startTime") LocalTime startTime, 
            @Param("endTime") LocalTime endTime);
}
