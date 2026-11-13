package com.eduplatform.observer;

import com.eduplatform.model.Grade;
import com.eduplatform.model.Notification;
import com.eduplatform.model.Student;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Concrete observer bound to one student. It ignores grades of other students and turns
 * the student's own grades into notifications in their inbox.
 */
public class StudentNotificationObserver implements GradeObserver {

    private final Student student;

    public StudentNotificationObserver(Student student) {
        this.student = Objects.requireNonNull(student, "Student must not be null");
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public void onGradePublished(Grade grade) {
        if (!grade.getStudent().equals(student)) {
            return;
        }
        student.receiveNotification(new Notification(student, grade, buildMessage(grade), LocalDateTime.now()));
    }

    private String buildMessage(Grade grade) {
        return String.format("Hello %s! Your result for quiz '%s' in course '%s' is %s.",
                student.getName(), grade.getQuiz().getTitle(), grade.getCourse().getTitle(), grade.getScoreSummary());
    }
}
