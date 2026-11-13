package edu.platform.recommendation;

import edu.platform.domain.Course;
import edu.platform.domain.StudentProfile;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

final class RecommendationSupport {
    private RecommendationSupport() {}
    static List<Course> eligibleCourses(StudentProfile student, Collection<Course> courses) {
        Objects.requireNonNull(student, "student must not be null");
        Objects.requireNonNull(courses, "courses must not be null");
        return courses.stream().map(course -> Objects.requireNonNull(course, "course must not be null"))
                .filter(Course::available).filter(course -> !student.completedCourseIds().contains(course.id())).toList();
    }
    static List<CourseRecommendation> rank(List<CourseRecommendation> candidates, int limit) {
        if (limit < 0) throw new IllegalArgumentException("limit must not be negative");
        if (limit == 0) return List.of();
        return candidates.stream().sorted(Comparator.comparingDouble(CourseRecommendation::score).reversed()
                        .thenComparing(recommendation -> recommendation.course().title().toLowerCase(Locale.ROOT))
                        .thenComparing(recommendation -> recommendation.course().id().value()))
                .limit(limit).toList();
    }
}
