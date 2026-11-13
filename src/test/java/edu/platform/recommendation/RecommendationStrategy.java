package edu.platform.recommendation;

import edu.platform.domain.Course;
import edu.platform.domain.StudentProfile;
import java.util.Collection;
import java.util.List;

/** One interchangeable course-ranking algorithm. */
public interface RecommendationStrategy {
    List<CourseRecommendation> recommend(StudentProfile student, Collection<Course> courses, int limit);
    String name();
}
