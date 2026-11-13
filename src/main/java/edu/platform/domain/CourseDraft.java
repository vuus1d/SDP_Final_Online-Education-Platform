package edu.platform.domain;

import java.util.Objects;
import java.util.Set;

/** Input to the teammate-owned Course Builder adapter. */
public record CourseDraft(CourseId id, String title, String description, Set<String> topics,
                          int minimumPerformance, boolean available) {
    public CourseDraft {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(topics, "topics must not be null");
        if (title.isBlank()) throw new IllegalArgumentException("title must not be blank");
        topics = Set.copyOf(topics);
        if (minimumPerformance < 0 || minimumPerformance > 100) throw new IllegalArgumentException("minimum performance must be between 0 and 100");
    }
}
