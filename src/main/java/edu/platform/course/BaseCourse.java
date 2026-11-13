package edu.platform.course;

import edu.platform.domain.Certificate;
import edu.platform.domain.Course;
import edu.platform.domain.CourseProgress;
import edu.platform.domain.LearningMaterial;
import edu.platform.domain.StudentId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Adapter from the shared immutable Course model to the decorated component contract. */
public final class BaseCourse implements CourseComponent {
    private final Course course;
    public BaseCourse(Course course) { this.course = Objects.requireNonNull(course, "course must not be null"); }
    @Override public Course course() { return course; }
    @Override public List<LearningMaterial> materials() { return List.of(); }
    @Override public boolean certificateEnabled() { return false; }
    @Override public Optional<Certificate> issueCertificate(StudentId studentId, CourseProgress progress) { return Optional.empty(); }
}
