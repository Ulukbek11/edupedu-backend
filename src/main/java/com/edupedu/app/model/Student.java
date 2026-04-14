package com.edupedu.app.model;


import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_group_id")
    private StudentGroup studentGroup;

    @OneToMany(mappedBy = "student")
    private List<CourseEnrollment> courseEnrollments;

    @OneToMany(mappedBy = "student")
    private List<CourseLessonProgress> courseLessonProgresses;

    @OneToMany(mappedBy = "student")
    private List<CourseTestAttempt> courseTestAttempts;

    // @ManyToMany(fetch = FetchType.LAZY)
    // @JoinTable(name = "student_classes", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "class_id"))
    // private List<Class> classes;

    // @OneToMany(mappedBy = "student")
    // // @Builder.Default
    // private List<TakenClass> takenClasses;

    @OneToMany(mappedBy = "student")
    private List<Semester> semesters;

    @Column(unique = true)
    private String studentNumber; // "Личность" студента в школе (например, S-2024-001)

    @Column(nullable = false, unique = true)
    private String accountNumber; // "Кошелек" студента для оплаты обучения (8 цифр)

    private String parentPhone;


    // @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private StudentStatus status = StudentStatus.ACTIVE;

}
