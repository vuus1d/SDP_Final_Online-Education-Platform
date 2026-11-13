package edu.platform.domain;

import java.util.Objects;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/** Recommendation projection of a student; registration and persistence belong to the Factory adapter. */
public record StudentProfile(StudentId id, Set<String> interests, double performanceScore,
                              Set<CourseId> completedCourseIds) {
    public StudentProfile {
        Objects.requireNonNull(id, "id must not be null");
        if (!Double.isFinite(performanceScore) || performanceScore < 0 || performanceScore > 100) {
            throw new IllegalArgumentException("performance score must be between 0 and 100");
        }
        Objects.requireNonNull(interests, "interests must not be null");
        interests = interests.stream().map(value -> requireText(value, "interest"))
                .map(value -> value.toLowerCase(Locale.ROOT)).collect(Collectors.toCollection(TreeSet::new));
        interests = Set.copyOf(interests);
        Objects.requireNonNull(completedCourseIds, "completed courses must not be null");
        completedCourseIds = Set.copyOf(completedCourseIds);
    }
    private static String requireText(String value, String name) {
        Objects.requireNonNull(value, name + " must not be null");
        if (value.isBlank()) throw new IllegalArgumentException(name + " must not be blank");
        return value.trim();
    }
}
