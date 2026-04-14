package com.edupedu.app.model;




import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CascadeType;

import com.edupedu.app.model.enums.ClassStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "classes")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "10A", "11B"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL)
    // // @Builder.Default
    // private List<Grade> grades = new ArrayList<>();

    @OneToMany(mappedBy = "clazz")
    // @Builder.Default
    private List<TakenClass> takenClasses;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    // @ManyToMany(mappedBy = "classes", fetch = FetchType.LAZY)
    // private List<Student> students;

    @OneToMany(mappedBy = "clazz")
    // @Builder.Default
    private List<Schedule> schedules;

    @Enumerated(EnumType.STRING)
    @Column(name = "class_status", nullable = false)
    private ClassStatus classStatus;

    private Short credits;

    // @OneToMany(mappedBy = "classGroup", cascade = CascadeType.ALL)
    // @Builder.Default
    // private List<Student> students = new ArrayList<>();
}
