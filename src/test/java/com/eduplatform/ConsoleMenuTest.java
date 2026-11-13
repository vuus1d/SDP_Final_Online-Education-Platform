package com.eduplatform;

import com.eduplatform.console.ConsoleInput;
import com.eduplatform.console.ConsoleMenu;
import com.eduplatform.observer.GradeService;
import com.eduplatform.service.CourseService;
import com.eduplatform.service.NotificationService;
import com.eduplatform.service.QuizService;
import com.eduplatform.service.UserService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Drives the real console menu with scripted input. */
class ConsoleMenuTest {

    private String runWithInput(String... lines) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        GradeService gradeService = new GradeService();
        ConsoleInput input = new ConsoleInput(new Scanner(String.join("\n", lines) + "\n"), out);
        new ConsoleMenu(input, out, new UserService(), new CourseService(), new QuizService(gradeService),
                gradeService, new NotificationService(gradeService)).run();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void demoWorkflowProducesNotification() {
        String output = runWithInput(
                "12",                // load demo data
                "7", "1", "1", "1",  // complete quiz: student Nurasyl, course, Java Basics Quiz
                "2", "1", "2",       // all answers correct
                "9", "1",            // view Nurasyl's notifications
                "0");

        assertTrue(output.contains("Demo data loaded"));
        assertTrue(output.contains("Quiz graded. Score: 3/3 (100%)"));
        assertTrue(output.contains("Quiz passed (minimum 70%); completion recorded."));
        assertTrue(output.contains("Observer delivered 1 new notification(s) to Nurasyl"));
        assertTrue(output.contains("Course: Java Programming | Quiz: Java Basics Quiz | Score: 3/3 (100%)"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void fullIntegratedWorkflowRunsThroughScriptedConsoleInput() {
        ArrayList<String> script = new ArrayList<>(List.of(
                "12",                         // demo data: Factory + Builder + Observer subscriptions
                "13", "1", "java, oop",      // real interests
                "14", "1",                   // interest recommendations
                "15", "2",                   // runtime switch to performance strategy
                "16", "1", "Study notes", "notes.pdf",
                "17", "1",                   // materials
                "18", "1",                   // certificate capability
                "20", "1", "1", "1",        // mark each of three lessons complete
                "20", "1", "1", "2",
                "20", "1", "1", "3",
                "7", "1", "1", "1", "2", "1", "2", // complete first quiz
                "7", "1", "1", "2", "2", "2",      // complete second quiz
                "19", "1", "1", "y",        // real progress and explicit certificate request
                "8", "1",                     // grades
                "9", "1",                     // notifications
                "0"));
        String output = runWithInput(script.toArray(String[]::new));
        assertTrue(output.contains("Demo data loaded"));
        assertTrue(output.contains("Matches interest: java"));
        assertTrue(output.contains("Additional material added."));
        assertTrue(output.contains("Certificate support enabled."));
        assertTrue(output.contains("Progress: 100%"));
        assertTrue(output.contains("Certificate: "));
        assertTrue(output.contains("Observer delivered 1 new notification(s)"));
        assertTrue(output.contains("Notifications for Nurasyl:"));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void registersUsersAndBuildsCourseInteractively() {
        String output = runWithInput(
                "1", "2", "John", "john@example.com",           // register teacher
                "1", "1", "Nurasyl", "nurasyl@example.com",     // register student
                "3", "1", "Java", "Basics", "30", "1", "Java", "0", // course plus recommendation topic/readiness
                "Intro", "Hello Java", "45", "n",               // one lesson
                "y", "Quiz 1", "2+2?", "2", "4", "5", "1", "n", // one quiz with one question
                "n",                                            // no more quizzes
                "5", "1", "1",                                  // enroll student
                "7", "1", "1", "1", "2",                        // take quiz, answer wrong
                "8", "1",                                       // view grades
                "0");

        assertTrue(output.contains("Student subscribed to grade notifications."));
        assertTrue(output.contains("Course created successfully: CRS-1 Java [BEGINNER, 30 h] by John"));
        assertTrue(output.contains("Nurasyl enrolled in 'Java'."));
        assertTrue(output.contains("Quiz graded. Score: 0/1 (0%)"));
        assertTrue(output.contains("Quiz below the 70% pass mark; course progress is unchanged for this quiz."));
        assertTrue(output.contains("Java | Quiz 1 | 0/1 (0%)"));
    }

    @Test
    void invalidInputDoesNotCrashTheApplication() {
        String output = runWithInput(
                "abc", "99",                               // invalid menu choices
                "1", "4", "1", "X", "not-an-email", // invalid role number, then invalid email
                "7",                                       // complete quiz with no students
                "0");

        assertTrue(output.contains("Please enter a whole number between 0 and 20."));
        assertTrue(output.contains("Error: Invalid email address"));
        assertTrue(output.contains("No students registered yet."));
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    void demoDataIsNotPartiallyLoadedWhenAnEmailIsTaken() {
        String output = runWithInput(
                "1", "2", "John", "john@example.com", // same email as the demo teacher
                "12",                                 // load demo data -> rejected
                "2",                                  // view all users
                "0");

        assertTrue(output.contains("Error: Cannot load demo data: email 'john@example.com' is already registered"));
        assertFalse(output.contains("Platform Admin"));
    }

    @Test
    void endOfInputExitsGracefully() {
        String output = runWithInput("2");

        assertTrue(output.contains("Input closed."));
    }
}
