package com.eduplatform;

import com.eduplatform.model.Course;
import com.eduplatform.model.Grade;
import com.eduplatform.model.Notification;
import com.eduplatform.model.Quiz;
import com.eduplatform.model.Student;
import com.eduplatform.model.Teacher;
import com.eduplatform.model.UserRole;
import com.eduplatform.observer.GradeService;
import com.eduplatform.service.CourseService;
import com.eduplatform.service.NotificationService;
import com.eduplatform.service.QuizService;
import com.eduplatform.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** End-to-end: Factory -> Builder -> enrollment -> quiz -> Observer notification. */
class EduPlatformWorkflowTest {

    private GradeService gradeService;
    private UserService userService;
    private CourseService courseService;
    private QuizService quizService;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        gradeService = new GradeService();
        userService = new UserService();
        courseService = new CourseService();
        quizService = new QuizService(gradeService);
        notificationService = new NotificationService(gradeService);
    }

    @Test
    void completeWorkflowDeliversNotificationToStudent() {
        Teacher teacher = (Teacher) userService.registerUser(UserRole.TEACHER, "John", "john@example.com");
        Student student = (Student) userService.registerUser(UserRole.STUDENT, "Nurasyl", "nurasyl@example.com");
        Student classmate = (Student) userService.registerUser(UserRole.STUDENT, "Aigerim", "aigerim@example.com");
        notificationService.subscribe(student);
        notificationService.subscribe(classmate);

        Quiz quiz = TestData.threeQuestionQuiz("Java Basics Quiz");
        Course course = courseService.addCourse(courseService.newCourseBuilder(teacher)
                .setTitle("Java Programming")
                .setDescription("Learn Java from basics to OOP")
                .addLesson(TestData.lesson("Intro"))
                .addLesson(TestData.lesson("OOP"))
                .addQuiz(quiz)
                .setDuration(30)
                .setDifficultyLevel("BEGINNER")
                .build());

        courseService.enroll(student, course);
        Grade grade = quizService.submitQuiz(student, course, quiz, TestData.allCorrectAnswers());

        assertEquals("CRS-1", course.getId());
        assertTrue(teacher.teaches(course));
        assertTrue(student.isEnrolledIn(course));
        assertEquals(100, grade.getScorePercentage());
        assertEquals(List.of(grade), gradeService.getGradesForStudent(student));

        List<Notification> notifications = notificationService.getNotifications(student);
        assertEquals(1, notifications.size());
        assertEquals("Java Programming", notifications.get(0).getCourseTitle());
        assertEquals("Java Basics Quiz", notifications.get(0).getQuizTitle());
        assertEquals("3/3 (100%)", notifications.get(0).getScoreSummary());
        assertTrue(classmate.getNotifications().isEmpty());
    }

    @Test
    void partiallyCorrectAnswersAreScored() {
        Teacher teacher = (Teacher) userService.registerUser(UserRole.TEACHER, "John", "john@example.com");
        Student student = (Student) userService.registerUser(UserRole.STUDENT, "Nurasyl", "nurasyl@example.com");
        Quiz quiz = TestData.threeQuestionQuiz("Quiz");
        Course course = courseService.addCourse(courseService.newCourseBuilder(teacher)
                .setTitle("Java").addLesson(TestData.lesson("Intro")).addQuiz(quiz)
                .setDuration(10).setDifficultyLevel("INTERMEDIATE").build());
        courseService.enroll(student, course);

        Grade grade = quizService.submitQuiz(student, course, quiz, List.of(0, 0, 1));

        assertEquals(1, grade.getCorrectAnswers());
        assertEquals(33, grade.getScorePercentage());
    }

    @Test
    void studentMustBeEnrolledToTakeQuiz() {
        Teacher teacher = (Teacher) userService.registerUser(UserRole.TEACHER, "John", "john@example.com");
        Student student = (Student) userService.registerUser(UserRole.STUDENT, "Nurasyl", "nurasyl@example.com");
        Quiz quiz = TestData.threeQuestionQuiz("Quiz");
        Course course = courseService.addCourse(courseService.newCourseBuilder(teacher)
                .setTitle("Java").addLesson(TestData.lesson("Intro")).addQuiz(quiz)
                .setDuration(10).setDifficultyLevel("BEGINNER").build());

        assertThrows(IllegalStateException.class,
                () -> quizService.submitQuiz(student, course, quiz, TestData.allCorrectAnswers()));
        assertTrue(gradeService.getAllGrades().isEmpty());
    }

    @Test
    void wrongNumberOfAnswersIsRejected() {
        Teacher teacher = (Teacher) userService.registerUser(UserRole.TEACHER, "John", "john@example.com");
        Student student = (Student) userService.registerUser(UserRole.STUDENT, "Nurasyl", "nurasyl@example.com");
        Quiz quiz = TestData.threeQuestionQuiz("Quiz");
        Course course = courseService.addCourse(courseService.newCourseBuilder(teacher)
                .setTitle("Java").addLesson(TestData.lesson("Intro")).addQuiz(quiz)
                .setDuration(10).setDifficultyLevel("BEGINNER").build());
        courseService.enroll(student, course);

        assertThrows(IllegalArgumentException.class,
                () -> quizService.submitQuiz(student, course, quiz, List.of(0)));
    }

    @Test
    void duplicateEmailAndDoubleEnrollmentAreRejected() {
        Teacher teacher = (Teacher) userService.registerUser(UserRole.TEACHER, "John", "john@example.com");
        Student student = (Student) userService.registerUser(UserRole.STUDENT, "Nurasyl", "nurasyl@example.com");
        Course course = courseService.addCourse(courseService.newCourseBuilder(teacher)
                .setTitle("Java").addLesson(TestData.lesson("Intro"))
                .setDuration(10).setDifficultyLevel("BEGINNER").build());
        courseService.enroll(student, course);

        assertThrows(IllegalStateException.class,
                () -> userService.registerUser(UserRole.STUDENT, "Other", "NURASYL@example.com"));
        assertThrows(IllegalStateException.class, () -> courseService.enroll(student, course));
        assertEquals(2, userService.getAllUsers().size());
    }
}
