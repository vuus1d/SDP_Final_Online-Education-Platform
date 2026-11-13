package edu.platform.domain;

import java.util.Objects;

public record CourseProgress(CourseId courseId, int completionPercent) {
    public CourseProgress {
        Objects.requireNonNull(courseId, "course id must not be null");
        if (completionPercent < 0 || completionPercent > 100) throw new IllegalArgumentException("completion must be between 0 and 100");
    }
    public boolean isComplete() { return completionPercent == 100; }
}
