package com.eduplatform.model;

import java.util.Objects;

public record LearningMaterial(String title, String location) {
    public LearningMaterial { Objects.requireNonNull(title); Objects.requireNonNull(location); if (title.isBlank() || location.isBlank()) throw new IllegalArgumentException("Material title and location must not be blank"); title = title.trim(); location = location.trim(); }
}
