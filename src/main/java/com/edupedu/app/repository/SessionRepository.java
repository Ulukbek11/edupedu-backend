package com.edupedu.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByScheduleId(Long scheduleId);
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s) FROM Session s WHERE s.schedule.clazz.teacher.id = :teacherId AND s.date BETWEEN :startDate AND :endDate")
    long countByTeacherAndDateRange(
            @org.springframework.data.repository.query.Param("teacherId") Long teacherId, 
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate, 
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate);
}
