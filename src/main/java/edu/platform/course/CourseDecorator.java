package edu.platform.course;

import edu.platform.domain.Certificate;
import edu.platform.domain.Course;
import edu.platform.domain.CourseProgress;
import edu.platform.domain.LearningMaterial;
import edu.platform.domain.StudentId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Base Decorator delegates the complete CourseComponent contract to its wrapped component. */
public abstract class CourseDecorator implements CourseComponent {
    private final CourseComponent delegate;
    protected CourseDecorator(CourseComponent delegate) { this.delegate = Objects.requireNonNull(delegate, "delegate must not be null"); }
    protected final CourseComponent delegate() { return delegate; }
    @Override public Course course() { return delegate.course(); }
    @Override public List<LearningMaterial> materials() { return delegate.materials(); }
    @Override public boolean certificateEnabled() { return delegate.certificateEnabled(); }
    @Override public Optional<Certificate> issueCertificate(StudentId studentId, CourseProgress progress) {
        return delegate.issueCertificate(studentId, progress);
    }
}
