package com.edupedu.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.Announcement;
import com.edupedu.app.model.enums.Role;

@Repository
public interface AnnouncementRepository  extends JpaRepository<Announcement, Long>{
    @Query("SELECT a FROM Announcement a WHERE " +
            "(a.targetRole IS NULL OR a.targetRole = :role) AND " +
            "(a.targetStudentGroup IS NULL OR a.targetStudentGroup.id = :studentGroupId) AND " +
            "a.createdAt <= :now AND " +
            "(a.expiresAt IS NULL OR a.expiresAt > :now) " +
            "ORDER BY a.important DESC, a.createdAt DESC")
    List<Announcement> findActiveAnnouncementsForStudent(
            @Param("role") Role role,
            @Param("studentGroupId") Long studentGroupId,
            @Param("now") LocalDateTime now);

    @Query("SELECT a FROM Announcement a WHERE " +
            "(a.targetRole IS NULL OR a.targetRole = :role) AND " +
            "a.createdAt <= :now AND " +
            "(a.expiresAt IS NULL OR a.expiresAt > :now) " +
            "ORDER BY a.important DESC, a.createdAt DESC")
    List<Announcement> findActiveAnnouncementsForRole(
            @Param("role") Role role,
            @Param("now") LocalDateTime now);

    List<Announcement> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    @Query("SELECT a FROM Announcement a ORDER BY a.important DESC, a.createdAt DESC")
    List<Announcement> findAllOrderByImportanceAndDate();
}
