package com.eduplatform.recommendation;

import com.eduplatform.model.Course;
import java.util.Collection;
import java.util.List;

public interface RecommendationStrategy {
    List<CourseRecommendation> recommend(StudentRecommendationProfile student, Collection<Course> courses, int limit);
    String name();
}
