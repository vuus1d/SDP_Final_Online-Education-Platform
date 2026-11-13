package com.eduplatform.recommendation;

import com.eduplatform.model.Student;
import java.util.Objects;

/** Recommendation view built from a registered Student and that student's persisted grades. */
public record StudentRecommendationProfile(Student student, double performanceScore) {
    public StudentRecommendationProfile {
        Objects.requireNonNull(student, "student must not be null");
        if (!Double.isFinite(performanceScore) || performanceScore < 0 || performanceScore > 100)
            throw new IllegalArgumentException("performance score must be between 0 and 100");
    }
}
