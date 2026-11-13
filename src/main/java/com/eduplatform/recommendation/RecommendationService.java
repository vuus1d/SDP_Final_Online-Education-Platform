package com.eduplatform.recommendation;

import com.eduplatform.model.Course;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/** Context for the Strategy pattern; its algorithm can be changed while the service is in use. */
public final class RecommendationService {
    private RecommendationStrategy strategy;
    public RecommendationService(RecommendationStrategy strategy) { setStrategy(strategy); }
    public synchronized void setStrategy(RecommendationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy must not be null");
    }
    public synchronized RecommendationStrategy strategy() { return strategy; }
    public List<CourseRecommendation> recommend(StudentRecommendationProfile student, Collection<Course> courses, int limit) {
        RecommendationStrategy selected;
        synchronized (this) { selected = strategy; }
        return selected.recommend(student, courses, limit);
    }
}
