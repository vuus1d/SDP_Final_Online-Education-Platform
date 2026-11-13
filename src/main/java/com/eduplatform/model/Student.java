package com.eduplatform.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.Map;

public class Student extends User {

    private final List<Course> enrolledCourses = new ArrayList<>();
    private final List<Notification> notifications = new ArrayList<>();
    private final Set<String> interests = new LinkedHashSet<>();
    private final Map<String, Set<String>> completedQuizzes = new HashMap<>();
    private final Map<String, Set<String>> completedLessons = new HashMap<>();

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

    public void setInterests(Set<String> values) {
        Objects.requireNonNull(values, "Interests must not be null");
        interests.clear();
        values.stream().map(value -> {
            if (value == null || value.isBlank()) throw new IllegalArgumentException("Interest must not be blank");
            return value.trim().toLowerCase(java.util.Locale.ROOT);
        }).forEach(interests::add);
    }
    public Set<String> getInterests() { return Collections.unmodifiableSet(interests); }
    public void markQuizCompleted(Course course, Quiz quiz) {
        if (!isEnrolledIn(course) || !course.containsQuiz(quiz)) throw new IllegalArgumentException("Quiz/course enrollment mismatch");
        completedQuizzes.computeIfAbsent(course.getId(), ignored -> new LinkedHashSet<>()).add(quiz.getTitle());
    }
    public void markLessonCompleted(Course course, Lesson lesson) {
        if (!isEnrolledIn(course) || !course.getLessons().contains(lesson)) throw new IllegalArgumentException("Lesson/course enrollment mismatch");
        completedLessons.computeIfAbsent(course.getId(), ignored -> new LinkedHashSet<>()).add(lesson.getTitle());
    }
    public int getCompletedActivities(Course course) {
        return completedQuizzes.getOrDefault(course.getId(), Set.of()).size()
                + completedLessons.getOrDefault(course.getId(), Set.of()).size();
    }
    public boolean hasCompletedCourse(Course course) {
        int total = course.getQuizzes().size() + course.getLessons().size();
        return total > 0 && getCompletedActivities(course) >= total;
    }
    public Set<String> getCompletedCourseIds() {
        return enrolledCourses.stream().filter(this::hasCompletedCourse).map(Course::getId)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
}
