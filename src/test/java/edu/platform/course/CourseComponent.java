package edu.platform.course;

import edu.platform.domain.Certificate;
import edu.platform.domain.Course;
import edu.platform.domain.CourseProgress;
import edu.platform.domain.LearningMaterial;
import edu.platform.domain.StudentId;
import java.util.List;
import java.util.Optional;

/** Component in the Course Decorator hierarchy. */
public interface CourseComponent {
    Course course();
    List<LearningMaterial> materials();
    boolean certificateEnabled();
    Optional<Certificate> issueCertificate(StudentId studentId, CourseProgress progress);
}
