package edu.platform.domain;

import java.util.Objects;

public record LearningMaterial(String title, String location) {
    public LearningMaterial {
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(location, "location must not be null");
        if (title.isBlank() || location.isBlank()) throw new IllegalArgumentException("material title and location must not be blank");
        title = title.trim();
        location = location.trim();
    }
}
