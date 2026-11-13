package com.eduplatform.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Teacher extends User {

    private final List<Course> courses = new ArrayList<>();

    public Teacher(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public UserRole getRole() {
        return UserRole.TEACHER;
    }

    @Override
    public String getDashboardSummary() {
        return String.format("Teaching %d course(s)", courses.size());
    }

    public void addCourse(Course course) {
        Objects.requireNonNull(course, "Course must not be null");
        if (!course.getTeacher().equals(this)) {
            throw new IllegalArgumentException("Course '" + course.getTitle() + "' is taught by another teacher");
        }
        if (!courses.contains(course)) {
            courses.add(course);
        }
    }

    public boolean teaches(Course course) {
        return courses.contains(course);
    }

    public List<Course> getCourses() {
        return Collections.unmodifiableList(courses);
    }
}
