package edu.platform.recommendation;

import edu.platform.domain.Course;
import edu.platform.domain.StudentProfile;
import java.util.Collection;
import java.util.List;

/** Recommends only courses whose prerequisite performance is met, favoring a suitable next challenge. */
public final class PerformanceBasedRecommendationStrategy implements RecommendationStrategy {
    @Override public List<CourseRecommendation> recommend(StudentProfile student, Collection<Course> courses, int limit) {
        if (limit < 0) throw new IllegalArgumentException("limit must not be negative");
        List<CourseRecommendation> candidates = RecommendationSupport.eligibleCourses(student, courses).stream()
                .filter(course -> student.performanceScore() >= course.minimumPerformance())
                .map(course -> {
                    double score = 100.0 - Math.abs(student.performanceScore() - course.minimumPerformance());
                    return new CourseRecommendation(course, score, List.of("Meets course readiness threshold: " + course.minimumPerformance()));
                }).toList();
        return RecommendationSupport.rank(candidates, limit);
    }
    @Override public String name() { return "Performance-based"; }
}
