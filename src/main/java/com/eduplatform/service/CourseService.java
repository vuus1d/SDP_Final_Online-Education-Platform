package com.eduplatform.service;

import com.eduplatform.model.Course;
import com.eduplatform.model.Student;
import com.eduplatform.model.Teacher;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Stores courses created with {@link Course.Builder} and handles student enrollment. */
public class CourseService {

    private static final String COURSE_ID_PREFIX = "CRS-";

    private final Map<String, Course> coursesById = new LinkedHashMap<>();

    /** Starts a course builder for the given teacher with the next free course id already set. */
    public Course.Builder newCourseBuilder(Teacher teacher) {
        Objects.requireNonNull(teacher, "Teacher must not be null");
        return Course.builder()
                .setId(nextCourseId())
                .setTeacher(teacher);
    }

    /** Registers a built course in the catalogue and links it to its teacher. */
    public Course addCourse(Course course) {
        Objects.requireNonNull(course, "Course must not be null");
        if (coursesById.containsKey(course.getId())) {
            throw new IllegalStateException("A course with id '" + course.getId() + "' already exists");
        }
        coursesById.put(course.getId(), course);
        course.getTeacher().addCourse(course);
        return course;
    }

    public void enroll(Student student, Course course) {
        Objects.requireNonNull(student, "Student must not be null");
        Objects.requireNonNull(course, "Course must not be null");
        Course registered = coursesById.get(course.getId());
        if (registered == null) {
            throw new IllegalArgumentException("Course '" + course.getTitle() + "' is not registered on the platform");
        }
        if (!registered.isAvailable()) throw new IllegalStateException("Course '" + course.getTitle() + "' is unavailable");
        student.enroll(registered);
    }

    public Optional<Course> findById(String id) {
        return Optional.ofNullable(coursesById.get(id));
    }

    public List<Course> getAllCourses() {
        return List.copyOf(coursesById.values());
    }

    private String nextCourseId() {
        return COURSE_ID_PREFIX + (coursesById.size() + 1);
    }
}
