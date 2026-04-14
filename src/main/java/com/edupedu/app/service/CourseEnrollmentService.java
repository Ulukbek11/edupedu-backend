package com.edupedu.app.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.Course;
import com.edupedu.app.model.CourseEnrollment;
import com.edupedu.app.model.Student;
import com.edupedu.app.repository.CourseEnrollmentRepository;
import com.edupedu.app.repository.CourseRepository;
import com.edupedu.app.repository.StudentRepository;
import com.edupedu.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseEnrollmentService {

    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CourseEnrollment enroll(Long studentId, Long courseId, String password) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalArgumentException("Already enrolled in this course");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (course.getEnrollmentPasswordHash() != null) {
            if (password == null || !passwordEncoder.matches(password, course.getEnrollmentPasswordHash())) {
                throw new IllegalArgumentException("Invalid enrollment password");
            }
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        CourseEnrollment enrollment = CourseEnrollment.builder()
                .student(student)
                .course(course)
                .build();
        return enrollmentRepository.save(enrollment);
    }

    public List<CourseEnrollment> getMyEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<CourseEnrollment> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }
}
