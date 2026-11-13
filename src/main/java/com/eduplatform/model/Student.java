package com.eduplatform.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Student extends User {

    private final List<Course> enrolledCourses = new ArrayList<>();
    private final List<Notification> notifications = new ArrayList<>();

    public Student(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public UserRole getRole() {
        return UserRole.STUDENT;
    }

    @Override
    public String getDashboardSummary() {
        return String.format("Enrolled in %d course(s), %d notification(s)",
                enrolledCourses.size(), notifications.size());
    }

    public void enroll(Course course) {
        Objects.requireNonNull(course, "Course must not be null");
        if (isEnrolledIn(course)) {
            throw new IllegalStateException(getName() + " is already enrolled in '" + course.getTitle() + "'");
        }
        enrolledCourses.add(course);
    }

    public boolean isEnrolledIn(Course course) {
        return enrolledCourses.contains(course);
    }

    public List<Course> getEnrolledCourses() {
        return Collections.unmodifiableList(enrolledCourses);
    }

    public void receiveNotification(Notification notification) {
        Objects.requireNonNull(notification, "Notification must not be null");
        if (!notification.getRecipient().equals(this)) {
            throw new IllegalArgumentException("Notification is addressed to another student");
        }
        notifications.add(notification);
    }

    public List<Notification> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }
}
