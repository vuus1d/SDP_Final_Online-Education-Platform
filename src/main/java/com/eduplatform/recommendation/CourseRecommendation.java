package com.eduplatform.recommendation;

import com.eduplatform.model.Course;
import java.util.List;
import java.util.Objects;

public record CourseRecommendation(Course course, double score, List<String> reasons) {
    public CourseRecommendation {
        Objects.requireNonNull(course, "course must not be null");
        if (!Double.isFinite(score)) throw new IllegalArgumentException("score must be finite");
        reasons = List.copyOf(Objects.requireNonNull(reasons, "reasons must not be null"));
    }
}
