package edu.platform.course;

import edu.platform.domain.Certificate;
import edu.platform.domain.CourseProgress;
import edu.platform.domain.StudentId;
import java.time.Clock;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Enables certificate issuance. Issuance is explicit and requires 100% progress for this course. */
public final class CertificateDecorator extends CourseDecorator {
    private final Clock clock;
    public CertificateDecorator(CourseComponent delegate) { this(delegate, Clock.systemUTC()); }
    public CertificateDecorator(CourseComponent delegate, Clock clock) {
        super(delegate);
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }
    @Override public boolean certificateEnabled() { return true; }
    @Override public Optional<Certificate> issueCertificate(StudentId studentId, CourseProgress progress) {
        Objects.requireNonNull(studentId, "student id must not be null");
        Objects.requireNonNull(progress, "progress must not be null");
        if (!progress.isComplete() || !course().id().equals(progress.courseId())) return Optional.empty();
        return Optional.of(new Certificate(UUID.randomUUID().toString(), studentId, course().id(), clock.instant()));
    }
}
