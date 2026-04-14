package com.edupedu.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edupedu.app.controller.CourseController.LessonDTO;
import com.edupedu.app.controller.CourseController.ModuleDTO;
import com.edupedu.app.controller.CourseController.TestRefDTO;
import com.edupedu.app.model.Course;
import com.edupedu.app.model.User;
import com.edupedu.app.model.enums.ContentType;
import com.edupedu.app.service.CourseService;

import com.edupedu.app.model.Module;
import com.edupedu.app.model.CourseLesson;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

        private final CourseService courseService;

        @PostMapping
        public ResponseEntity<CourseDTO> create(@AuthenticationPrincipal User user,
                        @RequestBody CreateCourseRequest request) {
                Course course = courseService.createCourse(
                                request.title(), request.description(), request.enrollmentPassword(),
                                request.isPublic(), user.getUniversity().getId(), user.getId());
                return ResponseEntity.ok(toDTO(course));
        }

        @GetMapping("/catalog")
        public ResponseEntity<List<CourseDTO>> catalog(@AuthenticationPrincipal User user) {
                Long universityId = user.getUniversity() != null ? user.getUniversity().getId() : null;
                List<CourseDTO> courses = courseService.findCatalog(universityId).stream()
                                .map(this::toDTO).toList();
                return ResponseEntity.ok(courses);
        }

        @GetMapping("/my")
        public ResponseEntity<List<CourseDTO>> myCourses(@AuthenticationPrincipal User user) {
                List<CourseDTO> courses = courseService.findByInstructor(user.getId()).stream()
                                .map(this::toDTO).toList();
                return ResponseEntity.ok(courses);
        }

        @GetMapping("/{id}")
        public ResponseEntity<CourseDetailDTO> getById(@PathVariable Long id) {
                Course course = courseService.findById(id);
                List<Module> modules = courseService.getModules(id);
                List<ModuleDTO> moduleDTOs = modules.stream().map(m -> {
                        List<LessonDTO> lessons = courseService.getLessons(m.getId()).stream()
                                        .map(this::toLessonDTO).toList();
                        List<TestRefDTO> tests = m.getCourseTests().stream()
                                        .map(t -> new TestRefDTO(t.getId(), t.getTitle())).toList();
                        return new ModuleDTO(m.getId(), m.getTitle(), m.getOrderIndex(), lessons, tests);
                }).toList();
                return ResponseEntity.ok(new CourseDetailDTO(toDTO(course), moduleDTOs));
        }

        @PutMapping("/{id}")
        public ResponseEntity<CourseDTO> update(@PathVariable Long id,
                        @AuthenticationPrincipal User user,
                        @RequestBody CreateCourseRequest request) {
                Course course = courseService.updateCourse(id, request.title(), request.description(),
                                request.enrollmentPassword(), request.isPublic(), user.getId());
                return ResponseEntity.ok(toDTO(course));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
                courseService.deleteCourse(id);
                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{courseId}/modules")
        public ResponseEntity<ModuleDTO> addModule(@PathVariable Long courseId,
                        @AuthenticationPrincipal User user,
                        @RequestBody CreateModuleRequest request) {
                Module module = courseService.addModule(courseId, request.title(), user.getId());
                return ResponseEntity
                                .ok(new ModuleDTO(module.getId(), module.getTitle(), module.getOrderIndex(), List.of(),
                                                List.of()));
        }

        @PostMapping("/{courseId}/modules/{moduleId}/lessons")
        public ResponseEntity<LessonDTO> addLesson(@PathVariable Long courseId,
                        @PathVariable Long moduleId,
                        @AuthenticationPrincipal User user,
                        @RequestBody CreateLessonRequest request) {
                CourseLesson lesson = courseService.addCourseLesson(moduleId, request.title(), request.content(),
                                request.contentType(), request.fileUrl(), user.getId());
                return ResponseEntity.ok(toLessonDTO(lesson));
        }

        private CourseDTO toDTO(Course c) {
                return new CourseDTO(c.getId(), c.getTitle(), c.getDescription(), c.getIsPublic(),
                                c.getEnrollmentPasswordHash() != null,
                                c.getTeacher().getUser().getFullName(), c.getTeacher().getId(),
                                c.getUniversity().getName(), c.getUniversity().getId());
        }

        private LessonDTO toLessonDTO(CourseLesson l) {
                return new LessonDTO(l.getId(), l.getTitle(), l.getContent(), l.getContentType().name(),
                                l.getFileUrl(), l.getOrderIndex());
        }

        public record CreateCourseRequest(String title, String description, String enrollmentPassword,
                        Boolean isPublic) {
        }

        public record CreateModuleRequest(String title) {
        }

        public record CreateLessonRequest(String title, String content, ContentType contentType, String fileUrl) {
        }

        public record CourseDTO(Long id, String title, String description, Boolean isPublic,
                        Boolean hasPassword, String instructorName, Long instructorId,
                        String universityName, Long universityId) {
        }

        public record ModuleDTO(Long id, String title, Integer orderIndex, List<LessonDTO> lessons,
                        List<TestRefDTO> tests) {
        }

        public record LessonDTO(Long id, String title, String content, String contentType, String fileUrl,
                        Integer orderIndex) {
        }

        public record TestRefDTO(Long id, String title) {
        }

        public record CourseDetailDTO(CourseDTO course, List<ModuleDTO> modules) {
        }
}
