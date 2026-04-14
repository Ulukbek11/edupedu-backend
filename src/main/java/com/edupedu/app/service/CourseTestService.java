package com.edupedu.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.CourseTest;
import com.edupedu.app.model.CourseTestAttempt;
import com.edupedu.app.model.CourseTestQuestion;
import com.edupedu.app.model.CourseTestQuestionChoice;
import com.edupedu.app.model.StudentAnswer;
import com.edupedu.app.model.enums.CourseTestQuestionType;
import com.edupedu.app.repository.CourseTestAttemptRepository;
import com.edupedu.app.repository.CourseTestQuestionChoiceRepository;
import com.edupedu.app.repository.CourseTestQuestionRepository;
import com.edupedu.app.repository.CourseTestRepository;
import com.edupedu.app.repository.ModuleRepository;
import com.edupedu.app.repository.StudentAnswerRepository;
import com.edupedu.app.repository.StudentRepository;
import com.edupedu.app.repository.UserRepository;
import com.edupedu.app.model.Module;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseTestService {

    private final CourseTestRepository courseTestRepository;
    private final CourseTestQuestionRepository courseTestQuestionRepository;
    private final CourseTestQuestionChoiceRepository choiceRepository;
    private final CourseTestAttemptRepository testAttemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public CourseTest createTest(Long moduleId, String title, Integer timeLimitMinutes,
            Boolean randomizeQuestions, Boolean randomizeChoices,
            Double passingScore, Long instructorId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", moduleId));

        // if (!canManageCourse(module.getCourse(), instructorId)) {
        //     throw new IllegalArgumentException("You can only create tests in your own courses");
        // }

        CourseTest test = CourseTest.builder()
                .title(title)
                .timeLimitMinutes(timeLimitMinutes)
                .randomizeQuestions(randomizeQuestions != null ? randomizeQuestions : false)
                .randomizeChoices(randomizeChoices != null ? randomizeChoices : false)
                .passingScore(passingScore != null ? passingScore : 60.0)
                .module(module)
                .build();
        return courseTestRepository.save(test);
    }

    @Transactional
    public CourseTestQuestion addQuestion(Long testId, String text, CourseTestQuestionType questionType,
            List<ChoiceInput> choices, Long instructorId) {
        CourseTest test = courseTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseTest", "id", testId));


        int nextOrder = test.getCourseTestQuestions().size();
        CourseTestQuestion question = CourseTestQuestion.builder()
                .text(text)
                .courseTestQuestionType(questionType)
                .orderIndex(nextOrder)
                .courseTest(test)
                .build();
        question = courseTestQuestionRepository.save(question);

        if (choices != null) {
            for (int i = 0; i < choices.size(); i++) {
                ChoiceInput ci = choices.get(i);
                CourseTestQuestionChoice choice = CourseTestQuestionChoice.builder()
                        .text(ci.text())
                        .isCorrect(ci.isCorrect())
                        .orderIndex(i)
                        .courseTestQuestion(question)
                        .build();
                choiceRepository.save(choice);
            }
        }

        return courseTestQuestionRepository.findById(question.getId()).orElse(question);
    }

    public CourseTest findTestById(Long testId) {
        return courseTestRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseTest", "id", testId));
    }

    @Transactional
    public CourseTestAttempt startAttempt(Long testId, Long studentId) {
        CourseTest test = findTestById(testId);

        CourseTestAttempt attempt = CourseTestAttempt.builder()
                .student(studentRepository.findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId)))
                .courseTest(test)
                .maxScore((double) test.getCourseTestQuestions().size())
                .build();
        return testAttemptRepository.save(attempt);
    }

    @Transactional
    public CourseTestAttempt submitAttempt(Long attemptId, List<AnswerInput> answers, Long studentId) {
        CourseTestAttempt attempt = testAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("CourseTestAttempt", "id", attemptId));

        if (!attempt.getStudent().getId().equals(studentId)) {
            throw new IllegalArgumentException("This is not your test attempt");
        }
        if (attempt.getSubmittedAt() != null) {
            throw new IllegalArgumentException("This attempt has already been submitted");
        }

        CourseTest test = attempt.getCourseTest();

        if (test.getTimeLimitMinutes() != null) {
            LocalDateTime deadline = attempt.getStartedAt().plusMinutes(test.getTimeLimitMinutes());
            if (LocalDateTime.now().isAfter(deadline)) {
                attempt.setSubmittedAt(deadline);
                attempt.setScore(0.0);
                return testAttemptRepository.save(attempt);
            }
        }

        double totalScore = 0.0;

        for (AnswerInput answerInput : answers) {
            CourseTestQuestion question = courseTestQuestionRepository.findById(answerInput.questionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question", "id", answerInput.questionId()));

            Set<CourseTestQuestionChoice> selectedChoices = new HashSet<>();
            if (answerInput.selectedChoiceIds() != null) {
                for (Long choiceId : answerInput.selectedChoiceIds()) {
                    CourseTestQuestionChoice choice = choiceRepository.findById(choiceId)
                            .orElseThrow(() -> new ResourceNotFoundException("Choice", "id", choiceId));
                    selectedChoices.add(choice);
                }
            }

            StudentAnswer studentAnswer = StudentAnswer.builder()
                    .testAttempt(attempt)
                    .courseTestQuestion(question)
                    .selectedChoices(selectedChoices)
                    .build();
            studentAnswerRepository.save(studentAnswer);

            Set<Long> correctChoiceIds = question.getCourseTestQuestionChoices().stream()
                    .filter(CourseTestQuestionChoice::getIsCorrect)
                    .map(CourseTestQuestionChoice::getId)
                    .collect(Collectors.toSet());

            Set<Long> selectedIds = selectedChoices.stream()
                    .map(CourseTestQuestionChoice::getId)
                    .collect(Collectors.toSet());

            if (question.getCourseTestQuestionType() == CourseTestQuestionType.SINGLE_CHOICE) {
                if (selectedIds.size() == 1 && correctChoiceIds.containsAll(selectedIds)) {
                    totalScore += 1.0;
                }
            } else if (question.getCourseTestQuestionType() == CourseTestQuestionType.MULTIPLE_CHOICE) {
                if (selectedIds.equals(correctChoiceIds)) {
                    totalScore += 1.0;
                }
            }
        }

        attempt.setScore(totalScore);
        attempt.setMaxScore((double) test.getCourseTestQuestions().size());
        attempt.setSubmittedAt(LocalDateTime.now());
        return testAttemptRepository.save(attempt);
    }

    public List<QuestionDTO> getQuestionsForStudent(Long testId) {
        CourseTest test = findTestById(testId);
        List<CourseTestQuestion> questions = new ArrayList<>(test.getCourseTestQuestions());

        if (Boolean.TRUE.equals(test.getRandomizeQuestions())) {
            Collections.shuffle(questions);
        }

        return questions.stream().map(q -> {
            List<ChoiceDTO> choices = new ArrayList<>(q.getCourseTestQuestionChoices().stream()
                    .map(c -> new ChoiceDTO(c.getId(), c.getText()))
                    .toList());
            if (Boolean.TRUE.equals(test.getRandomizeChoices())) {
                Collections.shuffle(choices);
            }
            return new QuestionDTO(q.getId(), q.getText(), q.getCourseTestQuestionType().name(), choices);
        }).toList();
    }

    public record ChoiceInput(String text, Boolean isCorrect) {
    }

    public record AnswerInput(Long questionId, List<Long> selectedChoiceIds) {
    }

    public record ChoiceDTO(Long id, String text) {
    }

    public record QuestionDTO(Long id, String text, String questionType, List<ChoiceDTO> choices) {
    }

    // private boolean canManageCourse(Course course, Long actorTeacherId) {
    //     if (course.getTeacher() != null && course.getTeacher().getId().equals(actorTeacherId)) {
    //         return true;
    //     }

    //     return teacherRepository.findById(actorTeacherId)
    //             .map(user -> user.getRole() == Role.ROLE_UNIVERSITY_ADMIN || user.getRole() == Role.ROLE_EOL_SUPER_ADMIN)
    //             .orElse(false);
    // }
}
