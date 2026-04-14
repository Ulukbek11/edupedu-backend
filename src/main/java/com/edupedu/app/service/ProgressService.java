package com.edupedu.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.Course;
import com.edupedu.app.model.CourseEnrollment;
import com.edupedu.app.model.CourseLesson;
import com.edupedu.app.model.CourseLessonProgress;
import com.edupedu.app.model.CourseTest;
import com.edupedu.app.model.CourseTestAttempt;
import com.edupedu.app.model.Student;
import com.edupedu.app.model.User;
import com.edupedu.app.repository.CourseEnrollmentRepository;
import com.edupedu.app.repository.CourseLessonProgressRepository;
import com.edupedu.app.repository.CourseLessonRepository;
import com.edupedu.app.repository.CourseRepository;
import com.edupedu.app.repository.CourseTestAttemptRepository;
import com.edupedu.app.repository.CourseTestRepository;
import com.edupedu.app.repository.StudentRepository;
import com.edupedu.app.repository.UserRepository;
import com.edupedu.app.service.ProgressService.CourseProgressDTO;
import com.edupedu.app.service.ProgressService.StudentProgressDTO;
import com.edupedu.app.service.ProgressService.TestScoreDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final CourseLessonProgressRepository lessonProgressRepository;
    private final CourseLessonRepository lessonRepository;
    private final CourseTestRepository courseTestRepository;
    private final CourseTestAttemptRepository testAttemptRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CourseEnrollmentRepository enrollmentRepository;

    @Transactional
    public CourseLessonProgress markLessonComplete(Long studentId, Long lessonId) {
        if (lessonProgressRepository.existsByStudentIdAndCourseLessonId(studentId, lessonId)) {
            return lessonProgressRepository.findByStudentIdAndCourseLessonId(studentId, lessonId).orElse(null);
        }

        CourseLesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        CourseLessonProgress progress = CourseLessonProgress.builder()
                .student(student)
                .courseLesson(lesson)
                .build();
        return lessonProgressRepository.save(progress);
    }

    public CourseProgressDTO getCourseProgress(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        long totalLessons = lessonRepository.countByCourseId(courseId);
        long totalTests = courseTestRepository.countByCourseId(courseId);
        long completedLessons = lessonProgressRepository.countCompletedByStudentAndCourse(studentId, courseId);

        List<CourseTest> courseTests = courseTestRepository.findByCourseId(courseId);
        long passedTests = 0;
        for (CourseTest test : courseTests) {
            List<CourseTestAttempt> attempts = testAttemptRepository
                    .findByStudentIdAndCourseTestId(studentId, test.getId());
            boolean passed = attempts.stream()
                    .filter(a -> a.getSubmittedAt() != null && a.getScore() != null && a.getMaxScore() != null)
                    .anyMatch(a -> a.getMaxScore() > 0
                            && (a.getScore() / a.getMaxScore() * 100) >= test.getPassingScore());
            if (passed)
                passedTests++;
        }

        long totalItems = totalLessons + totalTests;
        long completedItems = completedLessons + passedTests;
        double progressPercent = totalItems > 0 ? (double) completedItems / totalItems * 100.0 : 0.0;

        return new CourseProgressDTO(
                courseId,
                course.getTitle(),
                totalLessons,
                completedLessons,
                totalTests,
                passedTests,
                Math.round(progressPercent * 100.0) / 100.0);
    }

    public List<StudentProgressDTO> getStudentProgressForCourse(Long courseId) {
        List<CourseEnrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        return enrollments.stream().map(enrollment -> {
            Student student = enrollment.getStudent();
            CourseProgressDTO progress = getCourseProgress(student.getId(), courseId);

            List<CourseTestAttempt> attempts = testAttemptRepository
                    .findSubmittedByStudentAndCourse(student.getId(), courseId);
            List<TestScoreDTO> testScores = attempts.stream()
                    .map(a -> new TestScoreDTO(
                            a.getCourseTest().getTitle(),
                            a.getScore(),
                            a.getMaxScore(),
                            a.getSubmittedAt().toString()))
                    .toList();

            return new StudentProgressDTO(
                    student.getId(),
                    student.getUser().getFirstName() + " " + student.getUser().getLastName(),
                    student.getUser().getEmail(),
                    progress.progressPercent(),
                    testScores);
        }).collect(Collectors.toList());
    }

    public record CourseProgressDTO(
            Long courseId, String courseTitle,
            long totalLessons, long completedLessons,
            long totalTests, long passedTests,
            double progressPercent) {
    }

    public record StudentProgressDTO(
            Long studentId, String studentName, String email,
            double progressPercent, List<TestScoreDTO> testScores) {
    }

    public record TestScoreDTO(String testTitle, Double score, Double maxScore, String submittedAt) {
    }
}
