package edu.platform.domain;

import java.util.Objects;
import java.util.Set;

/** Input to the teammate-owned User Factory adapter. */
public record StudentRegistrationRequest(StudentId id, String name, Set<String> interests) {
    public StudentRegistrationRequest {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(interests, "interests must not be null");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        name = name.trim();
        interests = Set.copyOf(interests);
    }
}
