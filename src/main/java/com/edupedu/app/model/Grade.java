package com.edupedu.app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "grades")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "student_id", nullable = false)
    // private Student student;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "subject_id", nullable = false)
    // private Subject subject;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "teacher_id", nullable = false)
    // private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taken_class_id", nullable = false)
    private TakenClass takenClass;

    @Column(name = "grade_value", nullable = false)
    private Double value; // e.g., 85.5, 100.0

    @Column(name = "max_value", nullable = false)
    private Double maxValue; // e.g., 100.0

    private String gradeType; // e.g., "EXAM", "HOMEWORK", "QUIZ"

    private String description;

    @Column(nullable = false)
    private LocalDate date;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
