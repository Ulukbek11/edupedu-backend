package com.edupedu.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edupedu.app.model.User;
import com.edupedu.app.repository.StudentRepository;
import com.edupedu.app.request.AnnouncementDTO;
import com.edupedu.app.request.CreateAnnouncementRequest;
import com.edupedu.app.service.AnnouncementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final StudentRepository studentRepository;

    @GetMapping("/announcements")
    public ResponseEntity<List<AnnouncementDTO>> getMyAnnouncements(@AuthenticationPrincipal User user) {
        List<AnnouncementDTO> announcements;

        switch (user.getRole()) {
            case ROLE_STUDENT -> {
                var student = studentRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new IllegalStateException("Student profile not found"));
                announcements = announcementService.getAnnouncementsForStudent(student.getId());
            }
            case ROLE_TEACHER -> announcements = announcementService.getAnnouncementsForTeacher();
            case ROLE_UNIVERSITY_ADMIN, ROLE_ADMIN -> announcements = announcementService.getAllAnnouncements();
            default -> throw new IllegalStateException("Unknown role");
        }

        return ResponseEntity.ok(announcements);
    }

    @GetMapping("/announcements/all")
    public ResponseEntity<List<AnnouncementDTO>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @PostMapping("/admin/announcements")
    public ResponseEntity<AnnouncementDTO> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(announcementService.createAnnouncement(request, user.getId()));
    }

    @DeleteMapping("/admin/announcements/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
}
