package com.edupedu.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.Course;
import com.edupedu.app.model.CourseLesson;
import com.edupedu.app.model.University;
import com.edupedu.app.model.User;
import com.edupedu.app.repository.CourseLessonRepository;
import com.edupedu.app.repository.CourseRepository;
import com.edupedu.app.repository.ModuleRepository;
import com.edupedu.app.repository.TeacherRepository;
import com.edupedu.app.repository.UniversityRepository;
import com.edupedu.app.repository.UserRepository;
import com.edupedu.app.model.enums.Role;

import com.edupedu.app.model.enums.ContentType;
import com.edupedu.app.model.Module;
import com.edupedu.app.model.Teacher;
import com.edupedu.app.model.Course;
import com.edupedu.app.model.CourseLesson;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final CourseLessonRepository lessonRepository;
    private final UniversityRepository universityRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Course createCourse(String title, String description, String enrollmentPassword,
            Boolean isPublic, Long universityId, Long teacherId) {
        University university = universityRepository.findById(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("University", "id", universityId));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", teacherId));

        Course.CourseBuilder builder = Course.builder()
                .title(title)
                .description(description)
                .isPublic(isPublic != null ? isPublic : false)
                .university(university)
                .teacher(teacher);

        if (enrollmentPassword != null && !enrollmentPassword.isBlank()) {
            builder.enrollmentPasswordHash(passwordEncoder.encode(enrollmentPassword));
        }

        return courseRepository.save(builder.build());
    }

    @Transactional
    public Course updateCourse(Long courseId, String title, String description,
            String enrollmentPassword, Boolean isPublic, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        course.setTitle(title);
        course.setDescription(description);
        if (isPublic != null) {
            course.setIsPublic(isPublic);
        }
        if (enrollmentPassword != null) {
            if (enrollmentPassword.isBlank()) {
                course.setEnrollmentPasswordHash(null);
            } else {
                course.setEnrollmentPasswordHash(passwordEncoder.encode(enrollmentPassword));
            }
        }

        return courseRepository.save(course);
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    public List<Course> findByInstructor(Long instructorId) {
        return courseRepository.findByTeacherId(instructorId);
    }

    public List<Course> findCatalog(Long universityId) {
        if (universityId != null) {
            return courseRepository.findByIsPublicTrueAndUniversityId(universityId);
        }
        return courseRepository.findByIsPublicTrue();
    }

    public List<Course> findByUniversity(Long universityId) {
        return courseRepository.findByUniversityId(universityId);
    }

    @Transactional
    public Module addModule(Long courseId, String title, Long instructorId) {
        Course course = findById(courseId);


        int nextOrder = moduleRepository.countByCourseId(courseId);
        Module module = Module.builder()
                .title(title)
                .orderIndex(nextOrder)
                .course(course)
                .build();
        return moduleRepository.save(module);
    }

    @Transactional
    public CourseLesson addCourseLesson(Long moduleId, String title, String content, ContentType contentType,
            String fileUrl, Long instructorId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", moduleId));

        int nextOrder = lessonRepository.countByModuleId(moduleId);
        CourseLesson lesson = CourseLesson.builder()
                .title(title)
                .content(content)
                .contentType(contentType != null ? contentType : ContentType.TEXT)
                .fileUrl(fileUrl)
                .orderIndex(nextOrder)
                .module(module)
                .build();
        return lessonRepository.save(lesson);
    }

    public List<Module> getModules(Long courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
    }

    public List<CourseLesson> getLessons(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = findById(courseId);
        courseRepository.delete(course);
    }

    // private boolean canManageCourse(Course course, Long actorUserId) {
    //     if (course.getTeacher().getId().equals(actorUserId)) {
    //         return true;
    //     }

    //     return teacherRepository.findById(actorUserId)
    //             .map(teacher -> teacher.getRole() == Role.ROLE_UNIVERSITY_ADMIN || teacher.getRole() == Role.ROLE_SUPER_ADMIN)
    //             .orElse(false);
    // }
}
