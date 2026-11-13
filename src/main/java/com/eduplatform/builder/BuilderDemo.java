package com.eduplatform.builder;

import com.eduplatform.factory.UserFactory;
import com.eduplatform.model.Course;
import com.eduplatform.model.DifficultyLevel;
import com.eduplatform.model.Lesson;
import com.eduplatform.model.Question;
import com.eduplatform.model.Quiz;
import com.eduplatform.model.Teacher;

import java.util.List;

/**
 * Standalone demonstration of the Builder pattern using the production {@link Course.Builder}.
 */
public final class BuilderDemo {

    private BuilderDemo() {
    }

    public static void main(String[] args) {
        System.out.println("=== BUILDER PATTERN DEMO: course creation ===");

        Teacher teacher = UserFactory.createTeacher("Anna Lee", "anna@example.com");

        Course course = Course.builder()
                .setId("CRS-OOD")
                .setTitle("Object-Oriented Design")
                .setDescription("Design principles and classic design patterns in Java")
                .setTeacher(teacher)
                .addLesson(new Lesson("SOLID Principles", "Single responsibility, open/closed and more", 60))
                .addLesson(new Lesson("Creational Patterns", "Builder, Factory and Singleton", 75))
                .addLesson(new Lesson("Behavioral Patterns", "Observer, Strategy and Command", 80))
                .addQuiz(new Quiz("SOLID Quiz", List.of(
                        new Question("What does the 'S' in SOLID stand for?",
                                List.of("Single responsibility", "Static typing", "Subclassing"), 0),
                        new Question("Classes should be open for extension and closed for ...?",
                                List.of("inheritance", "modification"), 1))))
                .addQuiz(new Quiz("Design Patterns Quiz", List.of(
                        new Question("Which pattern builds complex objects step by step?",
                                List.of("Observer", "Builder", "Adapter"), 1))))
                .setDuration(24)
                .setDifficultyLevel(DifficultyLevel.INTERMEDIATE)
                .build();

        System.out.println("Course built successfully with method chaining:");
        printCourse(course);

        System.out.println();
        System.out.println("--- Validation: building a course with missing required fields ---");
        try {
            Course.builder()
                    .setTitle("Incomplete Course")
                    .setDuration(0)
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Builder rejected the invalid course:");
            System.out.println("  " + e.getMessage());
        }
    }

    private static void printCourse(Course course) {
        System.out.println("  ID:          " + course.getId());
        System.out.println("  Title:       " + course.getTitle());
        System.out.println("  Description: " + course.getDescription());
        System.out.println("  Teacher:     " + course.getTeacher().getName());
        System.out.println("  Duration:    " + course.getDurationHours() + " hours");
        System.out.println("  Difficulty:  " + course.getDifficultyLevel());
        System.out.println("  Lessons (" + course.getLessons().size() + "):");
        for (Lesson lesson : course.getLessons()) {
            System.out.println("    - " + lesson + ": " + lesson.getContent());
        }
        System.out.println("  Quizzes (" + course.getQuizzes().size() + "):");
        for (Quiz quiz : course.getQuizzes()) {
            System.out.println("    - " + quiz);
            for (Question question : quiz.getQuestions()) {
                System.out.println("        * " + question.getText() + " " + question.getOptions());
            }
        }
    }
}
