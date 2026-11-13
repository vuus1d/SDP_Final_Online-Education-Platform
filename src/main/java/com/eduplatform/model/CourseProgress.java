package com.eduplatform.model;

public record CourseProgress(String courseId, int completionPercent) {
    public CourseProgress { if (courseId == null || courseId.isBlank()) throw new IllegalArgumentException("Course id required"); if (completionPercent < 0 || completionPercent > 100) throw new IllegalArgumentException("Completion must be between 0 and 100"); }
    public boolean isComplete() { return completionPercent == 100; }
}
