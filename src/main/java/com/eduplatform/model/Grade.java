package com.eduplatform.model;

import java.time.LocalDateTime;
import java.util.Objects;

/** Immutable result of one quiz attempt by a student. */
public final class Grade {

    private static final int PERCENT = 100;

    private final Student student;
    private final Course course;
    private final Quiz quiz;
    private final int correctAnswers;
    private final LocalDateTime gradedAt;

    public Grade(Student student, Course course, Quiz quiz, int correctAnswers, LocalDateTime gradedAt) {
        this.student = Objects.requireNonNull(student, "Student must not be null");
        this.course = Objects.requireNonNull(course, "Course must not be null");
        this.quiz = Objects.requireNonNull(quiz, "Quiz must not be null");
        this.gradedAt = Objects.requireNonNull(gradedAt, "Grading time must not be null");
        if (!course.containsQuiz(quiz)) {
            throw new IllegalArgumentException("Quiz '" + quiz.getTitle() + "' does not belong to course '" + course.getTitle() + "'");
        }
        if (correctAnswers < 0 || correctAnswers > quiz.getQuestionCount()) {
            throw new IllegalArgumentException("Correct answers must be between 0 and " + quiz.getQuestionCount());
        }
        this.correctAnswers = correctAnswers;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getTotalQuestions() {
        return quiz.getQuestionCount();
    }

    public int getScorePercentage() {
        return (int) Math.round(correctAnswers * (double) PERCENT / getTotalQuestions());
    }

    public String getScoreSummary() {
        return String.format("%d/%d (%d%%)", correctAnswers, getTotalQuestions(), getScorePercentage());
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s", course.getTitle(), quiz.getTitle(), getScoreSummary());
    }
}
