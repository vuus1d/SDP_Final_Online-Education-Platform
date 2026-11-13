package edu.platform.domain;

import java.util.Objects;

public record CourseId(String value) {
    public CourseId {
        value = requireText(value, "course id");
    }
    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name + " must not be null");
        if (value.isBlank()) throw new IllegalArgumentException(name + " must not be blank");
        return value.trim();
    }
}
