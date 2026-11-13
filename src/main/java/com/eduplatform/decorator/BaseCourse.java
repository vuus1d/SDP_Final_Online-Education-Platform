package com.eduplatform.decorator;

import com.eduplatform.model.Course;
import com.eduplatform.model.Student;
import com.eduplatform.model.Certificate;
import com.eduplatform.model.LearningMaterial;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class BaseCourse implements CourseComponent {
    private final Course course;
    public BaseCourse(Course course) { this.course = Objects.requireNonNull(course); }
    public Course course() { return course; }
    public List<LearningMaterial> materials() { return List.of(); }
    public boolean certificateEnabled() { return false; }
    public Optional<Certificate> issueCertificate(Student student, int progressPercent) { return Optional.empty(); }
}
