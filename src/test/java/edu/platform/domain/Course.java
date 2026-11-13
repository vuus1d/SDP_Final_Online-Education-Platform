package edu.platform.domain;

import java.util.Objects;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/** Minimal immutable shared course contract; align with the team's Builder model during integration. */
public record Course(CourseId id, String title, String description, Set<String> topics,
                     int minimumPerformance, boolean available) {
    public Course {
        Objects.requireNonNull(id, "id must not be null");
        title = requireText(title, "title");
        description = Objects.requireNonNull(description, "description must not be null");
        if (minimumPerformance < 0 || minimumPerformance > 100) {
            throw new IllegalArgumentException("minimum performance must be between 0 and 100");
        }
        Objects.requireNonNull(topics, "topics must not be null");
        topics = topics.stream().map(topic -> requireText(topic, "topic"))
                .map(value -> value.toLowerCase(Locale.ROOT)).collect(Collectors.toCollection(TreeSet::new));
        topics = Set.copyOf(topics);
    }
    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name + " must not be null");
        if (value.isBlank()) throw new IllegalArgumentException(name + " must not be blank");
        return value.trim();
    }
}
