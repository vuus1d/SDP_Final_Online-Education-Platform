package com.eduplatform.model;

import java.time.Instant;
import java.util.Objects;

public record Certificate(String certificateId, String studentId, String courseId, Instant issuedAt) {
    public Certificate { Objects.requireNonNull(certificateId); Objects.requireNonNull(studentId); Objects.requireNonNull(courseId); Objects.requireNonNull(issuedAt); }
}
