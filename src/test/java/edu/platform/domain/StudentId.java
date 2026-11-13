package edu.platform.domain;

import java.util.Objects;

public record StudentId(String value) {
    public StudentId {
        Objects.requireNonNull(value, "student id must not be null");
        if (value.isBlank()) throw new IllegalArgumentException("student id must not be blank");
        value = value.trim();
    }
}
