package com.eduplatform;

import com.eduplatform.factory.UserFactory;
import com.eduplatform.model.Course;
import com.eduplatform.model.Grade;
import com.eduplatform.model.Notification;
import com.eduplatform.model.Quiz;
import com.eduplatform.model.Student;
import com.eduplatform.model.Teacher;
import com.eduplatform.observer.GradeObserver;
import com.eduplatform.observer.GradeService;
import com.eduplatform.observer.StudentNotificationObserver;
import com.eduplatform.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObserverTest {

    private GradeService gradeService;
    private Student alice;
    private Student bob;
    private Course course;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        gradeService = new GradeService();
        alice = UserFactory.createStudent("Alice", "alice@example.com");
        bob = UserFactory.createStudent("Bob", "bob@example.com");
        Teacher teacher = UserFactory.createTeacher("John", "john@example.com");
        quiz = TestData.threeQuestionQuiz("Java Basics Quiz");
        course = Course.builder()
                .setId("CRS-1").setTitle("Java Programming").setTeacher(teacher)
                .addLesson(TestData.lesson("Intro")).addQuiz(quiz)
                .setDuration(30).setDifficultyLevel("BEGINNER")
                .build();
    }

    private Grade gradeFor(Student student, int correctAnswers) {
        return new Grade(student, course, quiz, correctAnswers, LocalDateTime.now());
    }

    @Test
    void subscribedObserverIsRegistered() {
        StudentNotificationObserver observer = new StudentNotificationObserver(alice);

        gradeService.subscribe(observer);
        gradeService.subscribe(observer);

        assertTrue(gradeService.isSubscribed(observer));
        assertEquals(1, gradeService.getObserverCount());
    }

    @Test
    void unsubscribedObserverIsRemovedAndNoLongerNotified() {
        StudentNotificationObserver observer = new StudentNotificationObserver(alice);
        gradeService.subscribe(observer);

        gradeService.unsubscribe(observer);
        gradeService.recordGrade(gradeFor(alice, 3));

        assertFalse(gradeService.isSubscribed(observer));
        assertTrue(alice.getNotifications().isEmpty());
    }

    @Test
    void recordingGradeNotifiesAllObservers() {
        List<Grade> received = new ArrayList<>();
        GradeObserver auditObserver = received::add;
        gradeService.subscribe(auditObserver);
        gradeService.subscribe(new StudentNotificationObserver(alice));

        Grade grade = gradeFor(alice, 2);
        gradeService.recordGrade(grade);

        assertEquals(List.of(grade), received);
        assertEquals(1, alice.getNotifications().size());
        assertEquals(List.of(grade), gradeService.getGradesForStudent(alice));
    }

    @Test
    void onlyRelevantStudentReceivesNotification() {
        gradeService.subscribe(new StudentNotificationObserver(alice));
        gradeService.subscribe(new StudentNotificationObserver(bob));

        gradeService.recordGrade(gradeFor(alice, 3));

        assertEquals(1, alice.getNotifications().size());
        assertTrue(bob.getNotifications().isEmpty());
    }

    @Test
    void notificationContainsCourseQuizAndScore() {
        gradeService.subscribe(new StudentNotificationObserver(alice));

        gradeService.recordGrade(gradeFor(alice, 2));

        Notification notification = alice.getNotifications().get(0);
        assertEquals(alice, notification.getRecipient());
        assertEquals("Alice", notification.getStudentName());
        assertEquals("Java Programming", notification.getCourseTitle());
        assertEquals("Java Basics Quiz", notification.getQuizTitle());
        assertEquals(67, notification.getScorePercentage());
        assertEquals("2/3 (67%)", notification.getScoreSummary());
        assertTrue(notification.getMessage().contains("Alice"));
        assertTrue(notification.getMessage().contains("Java Basics Quiz"));
        assertTrue(notification.getMessage().contains("Java Programming"));
        assertTrue(notification.getMessage().contains("2/3 (67%)"));
    }

    @Test
    void notificationServiceManagesStudentSubscriptions() {
        NotificationService notificationService = new NotificationService(gradeService);

        assertTrue(notificationService.subscribe(alice));
        assertFalse(notificationService.subscribe(alice));
        assertTrue(notificationService.isSubscribed(alice));
        assertEquals(1, gradeService.getObserverCount());

        gradeService.recordGrade(gradeFor(alice, 1));
        assertEquals(1, notificationService.getNotifications(alice).size());

        assertTrue(notificationService.unsubscribe(alice));
        assertFalse(notificationService.unsubscribe(alice));
        assertEquals(0, gradeService.getObserverCount());

        gradeService.recordGrade(gradeFor(alice, 3));
        assertEquals(1, notificationService.getNotifications(alice).size());
        assertEquals(2, gradeService.getGradesForStudent(alice).size());
    }
}
