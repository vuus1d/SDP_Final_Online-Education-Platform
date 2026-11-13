package com.eduplatform;

import com.eduplatform.factory.UserFactory;
import com.eduplatform.model.*;
import com.eduplatform.observer.GradeObserver;
import com.eduplatform.observer.GradeService;
import com.eduplatform.observer.StudentNotificationObserver;
import com.eduplatform.service.CourseService;
import com.eduplatform.service.QuizService;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class StudentProgressRegressionTest {
    private static Course courseWith(Teacher teacher, List<Lesson> lessons, List<Quiz> quizzes) {
        Course.Builder builder = Course.builder().setId("progress-course").setTitle("Progress")
                .setTeacher(teacher).setDuration(2).setDifficultyLevel(DifficultyLevel.BEGINNER);
        if (lessons.isEmpty()) builder.addLesson(new Lesson("Course lesson", "", 10));
        lessons.forEach(builder::addLesson);
        quizzes.forEach(builder::addQuiz);
        return builder.build();
    }

    @Test void duplicateLessonTitlesAreTrackedByCoursePosition() {
        Student student = UserFactory.createStudent("Learner", "learner-progress@example.test");
        Teacher teacher = UserFactory.createTeacher("Teacher", "teacher-progress@example.test");
        Course course = courseWith(teacher, List.of(new Lesson("Review", "first", 10), new Lesson("Review", "second", 10)), List.of());
        CourseService courses = new CourseService(); courses.addCourse(course); courses.enroll(student, course);

        student.markLessonCompleted(course, 0);
        student.markLessonCompleted(course, 0);
        assertEquals(1, student.getCompletedActivities(course), "Repeating one lesson must not complete its same-title sibling");
        assertFalse(student.hasCompletedCourse(course));
        student.markLessonCompleted(course, 1);
        assertEquals(2, student.getCompletedActivities(course));
        assertTrue(student.hasCompletedCourse(course));
    }

    @Test void failedInterestReplacementLeavesPreviousInterestsUntouched() {
        Student student = UserFactory.createStudent("Learner", "learner-interests@example.test");
        student.setInterests(Set.of("Java", "Design"));
        LinkedHashSet<String> invalid = new LinkedHashSet<>(Arrays.asList("new-value", null));

        assertThrows(IllegalArgumentException.class, () -> student.setInterests(invalid));
        assertEquals(Set.of("java", "design"), student.getInterests());
    }

    @Test void quizCompletionRequiresSeventyPercentAndAllowsRetryAfterFailure() {
        Student student = UserFactory.createStudent("Learner", "learner-quiz@example.test");
        Teacher teacher = UserFactory.createTeacher("Teacher", "teacher-quiz@example.test");
        Quiz quiz = new Quiz("Ten questions", java.util.stream.IntStream.range(0, 10)
                .mapToObj(i -> new Question("Q" + i, List.of("right", "wrong"), 0)).toList());
        Course course = courseWith(teacher, List.of(), List.of(quiz));
        CourseService courses = new CourseService(); courses.addCourse(course); courses.enroll(student, course);
        GradeService grades = new GradeService(); QuizService quizService = new QuizService(grades);

        Grade failed = quizService.submitQuiz(student, course, quiz, List.of(0, 0, 0, 0, 0, 0, 1, 1, 1, 1));
        assertEquals(60, failed.getScorePercentage());
        assertFalse(failed.isPassing());
        assertEquals(0, student.getCompletedActivities(course), "A saved failing grade is not course completion");
        assertFalse(student.hasCompletedCourse(course));

        Grade passedAtThreshold = quizService.submitQuiz(student, course, quiz, List.of(0, 0, 0, 0, 0, 0, 0, 1, 1, 1));
        assertEquals(70, passedAtThreshold.getScorePercentage());
        assertTrue(passedAtThreshold.isPassing());
        assertEquals(1, student.getCompletedActivities(course));
        student.markLessonCompleted(course, 0);
        assertTrue(student.hasCompletedCourse(course));
        assertEquals(2, grades.getGradesForStudent(student).size(), "A retry is a distinct grade event");
    }

    @Test void observerRuntimeFailureIsRecordedWithoutLosingGradeCompletionOrLaterCallbacks() {
        Student student = UserFactory.createStudent("Learner", "learner-observer@example.test");
        Teacher teacher = UserFactory.createTeacher("Teacher", "teacher-observer@example.test");
        Quiz quiz = new Quiz("One question", List.of(new Question("Q", List.of("yes", "no"), 0)));
        Course course = courseWith(teacher, List.of(), List.of(quiz));
        CourseService courses = new CourseService(); courses.addCourse(course); courses.enroll(student, course);
        GradeService grades = new GradeService();
        grades.subscribe(grade -> { throw new IllegalStateException("observer unavailable"); });
        List<Grade> auditEvents = new ArrayList<>();
        GradeObserver audit = auditEvents::add;
        grades.subscribe(audit);
        grades.subscribe(new StudentNotificationObserver(student));

        Grade saved = new QuizService(grades).submitQuiz(student, course, quiz, List.of(0));

        assertEquals(List.of(saved), grades.getGradesForStudent(student));
        assertEquals(1, student.getCompletedActivities(course), "Passing quiz completion must commit despite observer failure");
        student.markLessonCompleted(course, 0);
        assertTrue(student.hasCompletedCourse(course));
        assertEquals(List.of(saved), auditEvents, "Observers after the failing observer must still run");
        assertEquals(1, student.getNotifications().size());
        assertEquals(1, grades.getObserverFailures().size());
        assertEquals(IllegalStateException.class, grades.getObserverFailures().getFirst().cause().getClass());
        assertThrows(IllegalStateException.class, () -> grades.recordGrade(saved), "Replaying the same grade event is rejected");
        assertThrows(IllegalStateException.class, () -> grades.notifyObservers(saved), "A saved grade event cannot be published twice");
        assertEquals(1, auditEvents.size());
        assertEquals(1, student.getNotifications().size());
    }

    @Test void invalidQuizSubmissionDoesNotCommitGradeOrProgress() {
        Student student = UserFactory.createStudent("Learner", "learner-invalid@example.test");
        Teacher teacher = UserFactory.createTeacher("Teacher", "teacher-invalid@example.test");
        Quiz quiz = new Quiz("One question", List.of(new Question("Q", List.of("yes", "no"), 0)));
        Course course = courseWith(teacher, List.of(), List.of(quiz));
        CourseService courses = new CourseService(); courses.addCourse(course); courses.enroll(student, course);
        GradeService grades = new GradeService();

        assertThrows(IllegalArgumentException.class, () -> new QuizService(grades).submitQuiz(student, course, quiz, List.of()));
        assertTrue(grades.getAllGrades().isEmpty());
        assertEquals(0, student.getCompletedActivities(course));
    }
}
