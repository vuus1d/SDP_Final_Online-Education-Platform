package edu.platform.domain;

import java.util.Objects;

public record QuizResult(String quizId, StudentId studentId, CourseId courseId, double scorePercent) {
    public QuizResult {
        Objects.requireNonNull(quizId, "quiz id must not be null");
        Objects.requireNonNull(studentId, "student id must not be null");
        Objects.requireNonNull(courseId, "course id must not be null");
        if (quizId.isBlank()) throw new IllegalArgumentException("quiz id must not be blank");
        if (!Double.isFinite(scorePercent) || scorePercent < 0 || scorePercent > 100) throw new IllegalArgumentException("score must be between 0 and 100");
    }
}
