package com.eduplatform.recommendation;

import com.eduplatform.model.Course;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Ranks available, incomplete courses by the number and proportion of matching interests. */
public final class InterestBasedRecommendationStrategy implements RecommendationStrategy {
    @Override public List<CourseRecommendation> recommend(StudentRecommendationProfile profile, Collection<Course> courses, int limit) {
        if (limit < 0) throw new IllegalArgumentException("limit must not be negative");
        if (profile == null) throw new NullPointerException("student must not be null");
        var student = profile.student();
        if (limit == 0 || courses == null) {
            if (courses == null) throw new NullPointerException("courses must not be null");
            return List.of();
        }
        List<CourseRecommendation> candidates = RecommendationSupport.eligibleCourses(profile, courses).stream()
                .map(course -> {
                    Set<String> matches = course.getTopics().stream().filter(student.getInterests()::contains).collect(Collectors.toSet());
                    double score = matches.isEmpty() ? 0 : 100.0 * matches.size() / Math.max(1, student.getInterests().size());
                    return new CourseRecommendation(course, score, matches.stream().sorted().map(topic -> "Matches interest: " + topic).toList());
                }).filter(recommendation -> recommendation.score() > 0).toList();
        return RecommendationSupport.rank(candidates, limit);
    }
    @Override public String name() { return "Interest-based"; }
}
