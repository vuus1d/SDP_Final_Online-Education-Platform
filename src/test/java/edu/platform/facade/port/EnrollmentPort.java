package edu.platform.facade.port;

import edu.platform.domain.CourseId;
import edu.platform.domain.StudentId;

@FunctionalInterface
public interface EnrollmentPort {
    void enroll(StudentId studentId, CourseId courseId);
}
