package com.edupedu.app.controller;

import java.util.List;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edupedu.app.model.CourseTest;
import com.edupedu.app.model.CourseTestAttempt;
import com.edupedu.app.model.CourseTestQuestion;
import com.edupedu.app.model.User;
import com.edupedu.app.model.enums.CourseTestQuestionType;
import com.edupedu.app.service.CourseTestService;
import com.edupedu.app.service.CourseTestService.AnswerInput;
import com.edupedu.app.service.CourseTestService.ChoiceInput;
import com.edupedu.app.service.CourseTestService.QuestionDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tests")
@RequiredArgsConstructor
public class TestController {

        private final CourseTestService testService;

        @PostMapping
        public ResponseEntity<TestDTO> createTest(@AuthenticationPrincipal User user,
                        @RequestBody CreateTestRequest request) {
                CourseTest test = testService.createTest(
                                request.moduleId(), request.title(), request.timeLimitMinutes(),
                                request.randomizeQuestions(), request.randomizeChoices(),
                                request.passingScore(), user.getId());
                return ResponseEntity.ok(toDTO(test));
        }

        @PostMapping("/{testId}/questions")
        public ResponseEntity<QuestionResponseDTO> addQuestion(@PathVariable Long testId,
                        @AuthenticationPrincipal User user,
                        @RequestBody AddQuestionRequest request) {
                CourseTestQuestion question = testService.addQuestion(testId, request.text(), request.questionType(),
                                request.choices(), user.getId());
                return ResponseEntity.ok(new QuestionResponseDTO(question.getId(), question.getText(),
                                question.getCourseTestQuestionType().name(), question.getOrderIndex()));
        }

        @GetMapping("/{testId}")
        public ResponseEntity<TestDTO> getTest(@PathVariable Long testId) {
                CourseTest test = testService.findTestById(testId);
                return ResponseEntity.ok(toDTO(test));
        }

        @GetMapping("/{testId}/questions")
        public ResponseEntity<List<QuestionDTO>> getQuestions(@PathVariable Long testId) {
                return ResponseEntity.ok(testService.getQuestionsForStudent(testId));
        }

        @PostMapping("/{testId}/attempts")
        public ResponseEntity<AttemptDTO> startAttempt(@PathVariable Long testId,
                        @AuthenticationPrincipal User user) {
                CourseTestAttempt attempt = testService.startAttempt(testId, user.getId());
                return ResponseEntity.ok(new AttemptDTO(attempt.getId(), attempt.getStartedAt().toString(),
                                null, null, null));
        }

        @PostMapping("/attempts/{attemptId}/submit")
        public ResponseEntity<AttemptDTO> submitAttempt(@PathVariable Long attemptId,
                        @AuthenticationPrincipal User user,
                        @RequestBody SubmitAnswersRequest request) {
                CourseTestAttempt attempt = testService.submitAttempt(attemptId, request.answers(), user.getId());
                return ResponseEntity.ok(new AttemptDTO(
                                attempt.getId(),
                                attempt.getStartedAt().toString(),
                                attempt.getSubmittedAt() != null ? attempt.getSubmittedAt().toString() : null,
                                attempt.getScore(),
                                attempt.getMaxScore()));
        }

        private TestDTO toDTO(CourseTest test) {
                return new TestDTO(test.getId(), test.getTitle(), test.getTimeLimitMinutes(),
                                test.getRandomizeQuestions(), test.getRandomizeChoices(), test.getPassingScore(),
                                test.getCourseTestQuestions().size());
        }

        public record CreateTestRequest(Long moduleId, String title, Integer timeLimitMinutes,
                        Boolean randomizeQuestions, Boolean randomizeChoices, Double passingScore) {
        }

        public record AddQuestionRequest(String text, CourseTestQuestionType questionType, List<ChoiceInput> choices) {
        }

        public record SubmitAnswersRequest(List<AnswerInput> answers) {
        }

        public record TestDTO(Long id, String title, Integer timeLimitMinutes,
                        Boolean randomizeQuestions, Boolean randomizeChoices, Double passingScore,
                        int questionCount) {
        }

        public record QuestionResponseDTO(Long id, String text, String questionType, Integer orderIndex) {
        }

        public record AttemptDTO(Long id, String startedAt, String submittedAt, Double score, Double maxScore) {
        }
}
