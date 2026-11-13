package com.eduplatform.decorator;

import com.eduplatform.model.Course;
import com.eduplatform.model.Student;
import com.eduplatform.model.Certificate;
import com.eduplatform.model.LearningMaterial;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class CourseDecorator implements CourseComponent {
    private final CourseComponent delegate;
    protected CourseDecorator(CourseComponent delegate) { this.delegate = Objects.requireNonNull(delegate); }
    protected CourseComponent delegate() { return delegate; }
    public Course course() { return delegate.course(); }
    public List<LearningMaterial> materials() { return delegate.materials(); }
    public boolean certificateEnabled() { return delegate.certificateEnabled(); }
    public Optional<Certificate> issueCertificate(Student student, int progressPercent) { return delegate.issueCertificate(student, progressPercent); }
}
