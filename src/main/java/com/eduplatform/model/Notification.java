package com.eduplatform.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/** Message delivered to a student's inbox when one of their grades is published. */
public final class Notification {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Student recipient;
    private final Grade grade;
    private final String message;
    private final LocalDateTime createdAt;

    public Notification(Student recipient, Grade grade, String message, LocalDateTime createdAt) {
        this.recipient = Objects.requireNonNull(recipient, "Recipient must not be null");
        this.grade = Objects.requireNonNull(grade, "Grade must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Creation time must not be null");
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Notification message must not be empty");
        }
        this.message = message;
    }

    public Student getRecipient() {
        return recipient;
    }

    public String getStudentName() {
        return recipient.getName();
    }

    public String getCourseTitle() {
        return grade.getCourse().getTitle();
    }

    public String getQuizTitle() {
        return grade.getQuiz().getTitle();
    }

    public int getScorePercentage() {
        return grade.getScorePercentage();
    }

    public String getScoreSummary() {
        return grade.getScoreSummary();
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "[" + createdAt.format(TIME_FORMAT) + "] " + message;
    }
}
