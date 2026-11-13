package edu.platform.domain;

import java.time.Instant;
import java.util.Objects;

public record Certificate(String certificateId, StudentId studentId, CourseId courseId, Instant issuedAt) {
    public Certificate {
        Objects.requireNonNull(certificateId, "certificate id must not be null");
        Objects.requireNonNull(studentId, "student id must not be null");
        Objects.requireNonNull(courseId, "course id must not be null");
        Objects.requireNonNull(issuedAt, "issued at must not be null");
    }
}
