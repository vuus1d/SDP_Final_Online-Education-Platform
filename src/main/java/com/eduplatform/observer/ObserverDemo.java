package com.eduplatform.observer;

import com.eduplatform.factory.UserFactory;
import com.eduplatform.model.Course;
import com.eduplatform.model.DifficultyLevel;
import com.eduplatform.model.Grade;
import com.eduplatform.model.Lesson;
import com.eduplatform.model.Notification;
import com.eduplatform.model.Question;
import com.eduplatform.model.Quiz;
import com.eduplatform.model.Student;
import com.eduplatform.model.Teacher;
import com.eduplatform.service.CourseService;
import com.eduplatform.service.QuizService;

import java.util.List;

/**
 * Standalone demonstration of the Observer pattern using the production {@link GradeService} (subject)
 * and {@link StudentNotificationObserver} (observer).
 */
public final class ObserverDemo {

    private ObserverDemo() {
    }

    public static void main(String[] args) {
        System.out.println("=== OBSERVER PATTERN DEMO: grade notifications ===");

        GradeService gradeService = new GradeService();
        CourseService courseService = new CourseService();
        QuizService quizService = new QuizService(gradeService);

        Teacher teacher = UserFactory.createTeacher("John Smith", "john@example.com");
        Student nurasyl = UserFactory.createStudent("Nurasyl", "nurasyl@example.com");
        Student aigerim = UserFactory.createStudent("Aigerim", "aigerim@example.com");

        Quiz quiz = new Quiz("Java Basics Quiz", List.of(
                new Question("Which keyword creates an object?", List.of("class", "new", "this"), 1),
                new Question("What runs Java bytecode?", List.of("JDK compiler", "JVM", "IDE"), 1)));
        Course course = courseService.addCourse(courseService.newCourseBuilder(teacher)
                .setTitle("Java Programming")
                .addLesson(new Lesson("Introduction to Java", "JDK, JVM and your first program", 45))
                .addQuiz(quiz)
                .setDuration(30)
                .setDifficultyLevel(DifficultyLevel.BEGINNER)
                .build());
        courseService.enroll(nurasyl, course);
        courseService.enroll(aigerim, course);

        System.out.println();
        System.out.println("Step 1. Subscribe one observer per student to the GradeService (subject)");
        StudentNotificationObserver nurasylObserver = new StudentNotificationObserver(nurasyl);
        StudentNotificationObserver aigerimObserver = new StudentNotificationObserver(aigerim);
        gradeService.subscribe(nurasylObserver);
        gradeService.subscribe(aigerimObserver);
        System.out.println("  Registered observers: " + gradeService.getObserverCount());

        System.out.println();
        System.out.println("Step 2. Nurasyl completes the quiz -> grade is recorded and published");
        Grade nurasylGrade = quizService.submitQuiz(nurasyl, course, quiz, List.of(1, 1));
        System.out.println("  Grade recorded: " + nurasylGrade);
        printInbox(nurasyl);
        printInbox(aigerim);
        System.out.println("  -> Only the relevant student was notified.");

        System.out.println();
        System.out.println("Step 3. Aigerim unsubscribes, then completes the quiz");
        gradeService.unsubscribe(aigerimObserver);
        System.out.println("  Registered observers: " + gradeService.getObserverCount());
        Grade aigerimGrade = quizService.submitQuiz(aigerim, course, quiz, List.of(1, 0));
        System.out.println("  Grade recorded: " + aigerimGrade);
        printInbox(aigerim);
        System.out.println("  -> The grade was saved, but no notification was sent after unsubscribing.");
        System.out.println("  Grades stored for Aigerim: " + gradeService.getGradesForStudent(aigerim).size());
    }

    private static void printInbox(Student student) {
        List<Notification> notifications = student.getNotifications();
        System.out.println("  " + student.getName() + "'s inbox: " + notifications.size() + " notification(s)");
        for (Notification notification : notifications) {
            System.out.println("    " + notification);
            System.out.println("      Student: " + notification.getStudentName()
                    + " | Course: " + notification.getCourseTitle()
                    + " | Quiz: " + notification.getQuizTitle()
                    + " | Score: " + notification.getScoreSummary());
        }
    }
}
