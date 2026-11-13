package edu.platform.recommendation;

import edu.platform.domain.Course;
import edu.platform.domain.StudentProfile;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Ranks available, incomplete courses by the number and proportion of matching interests. */
public final class InterestBasedRecommendationStrategy implements RecommendationStrategy {
    @Override public List<CourseRecommendation> recommend(StudentProfile student, Collection<Course> courses, int limit) {
        if (limit < 0) throw new IllegalArgumentException("limit must not be negative");
        if (limit == 0 || student == null || courses == null) {
            if (student == null) throw new NullPointerException("student must not be null");
            if (courses == null) throw new NullPointerException("courses must not be null");
            return List.of();
        }
        List<CourseRecommendation> candidates = RecommendationSupport.eligibleCourses(student, courses).stream()
                .map(course -> {
                    Set<String> matches = course.topics().stream().filter(student.interests()::contains).collect(Collectors.toSet());
                    double score = matches.isEmpty() ? 0 : 100.0 * matches.size() / Math.max(1, student.interests().size());
                    return new CourseRecommendation(course, score, matches.stream().sorted().map(topic -> "Matches interest: " + topic).toList());
                }).filter(recommendation -> recommendation.score() > 0).toList();
        return RecommendationSupport.rank(candidates, limit);
    }
    @Override public String name() { return "Interest-based"; }
}
