package edu.platform.facade.port;

import edu.platform.domain.StudentId;
import edu.platform.domain.StudentProfile;
import java.util.Optional;

@FunctionalInterface
public interface StudentDirectoryPort {
    Optional<StudentProfile> findStudent(StudentId id);
}
