package com.eduplatform.decorator;

import com.eduplatform.model.Student;
import com.eduplatform.model.Certificate;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class CertificateDecorator extends CourseDecorator {
    private final Clock clock;
    private final Map<String, Certificate> issued = new HashMap<>();
    public CertificateDecorator(CourseComponent delegate) { this(delegate, Clock.systemUTC()); }
    public CertificateDecorator(CourseComponent delegate, Clock clock) { super(delegate); this.clock = Objects.requireNonNull(clock); }
    public boolean certificateEnabled() { return true; }
    public synchronized Optional<Certificate> issueCertificate(Student student, int progressPercent) {
        Objects.requireNonNull(student);
        if (!student.isEnrolledIn(course()) || progressPercent < 100 || progressPercent > 100) return Optional.empty();
        return Optional.of(issued.computeIfAbsent(student.getId(), id ->
                new Certificate(UUID.randomUUID().toString(), id, course().getId(), clock.instant())));
    }
}
