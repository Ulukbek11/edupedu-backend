package com.edupedu.app.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;


import com.edupedu.app.model.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "announcements")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    private Role targetRole; // null means for everyone

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_student_group_id")
    private StudentGroup targetStudentGroup; // null means for all classes

    private Boolean important;

    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // @CreatedDate
    // @Column(name = "created_at")
    // private LocalDateTime createdAt;

    // @LastModifiedDate
    // @Column(name = "last_modified_at")
    // private LocalDateTime lastModifiedAt;

    // @PrePersist
    // protected void onCreate() {
    //     createdAt = LocalDateTime.now();
    // }

    // @PreUpdate
    // protected void onUpdate() {
    //     lastModifiedAt = LocalDateTime.now();
    // }


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (important == null) {
            important = false;
        }
    }
}
