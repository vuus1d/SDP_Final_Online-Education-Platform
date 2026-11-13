package edu.platform.facade.port;

import edu.platform.domain.CourseId;
import edu.platform.domain.CourseProgress;
import edu.platform.domain.StudentId;

@FunctionalInterface
public interface ProgressQueryPort {
    CourseProgress progress(StudentId studentId, CourseId courseId);
}
