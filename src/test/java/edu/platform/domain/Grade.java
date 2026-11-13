package edu.platform.domain;

import java.util.Objects;

public record Grade(StudentId studentId, CourseId courseId, double scorePercent, String feedback) {
    public Grade {
        Objects.requireNonNull(studentId, "student id must not be null");
        Objects.requireNonNull(courseId, "course id must not be null");
        Objects.requireNonNull(feedback, "feedback must not be null");
        if (!Double.isFinite(scorePercent) || scorePercent < 0 || scorePercent > 100) throw new IllegalArgumentException("grade must be between 0 and 100");
    }
}
