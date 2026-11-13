package edu.platform.facade.port;

import edu.platform.domain.Course;
import edu.platform.domain.CourseId;
import java.util.List;
import java.util.Optional;

public interface CourseCatalogPort {
    Optional<Course> findCourse(CourseId id);
    List<Course> availableCourses();
    void saveCourse(Course course);
}
