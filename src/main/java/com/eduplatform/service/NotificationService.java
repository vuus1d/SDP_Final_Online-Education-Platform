package com.eduplatform.service;

import com.eduplatform.model.Notification;
import com.eduplatform.model.Student;
import com.eduplatform.observer.GradeSubject;
import com.eduplatform.observer.StudentNotificationObserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Manages which students are subscribed (as observers) to grade notifications. */
public class NotificationService {

    private final GradeSubject gradeSubject;
    private final Map<Student, StudentNotificationObserver> observersByStudent = new HashMap<>();

    public NotificationService(GradeSubject gradeSubject) {
        this.gradeSubject = Objects.requireNonNull(gradeSubject, "Grade subject must not be null");
    }

    /** @return true if the student was subscribed now, false if already subscribed */
    public boolean subscribe(Student student) {
        Objects.requireNonNull(student, "Student must not be null");
        if (isSubscribed(student)) {
            return false;
        }
        StudentNotificationObserver observer = new StudentNotificationObserver(student);
        gradeSubject.subscribe(observer);
        observersByStudent.put(student, observer);
        return true;
    }

    /** @return true if the student was unsubscribed now, false if they were not subscribed */
    public boolean unsubscribe(Student student) {
        StudentNotificationObserver observer = observersByStudent.remove(student);
        if (observer == null) {
            return false;
        }
        gradeSubject.unsubscribe(observer);
        return true;
    }

    public boolean isSubscribed(Student student) {
        return observersByStudent.containsKey(student);
    }

    public List<Notification> getNotifications(Student student) {
        Objects.requireNonNull(student, "Student must not be null");
        return student.getNotifications();
    }
}
