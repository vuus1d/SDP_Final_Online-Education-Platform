package edu.platform.facade.port;

import edu.platform.domain.StudentProfile;
import edu.platform.domain.StudentRegistrationRequest;

/** Adapter to the team's Factory; the concrete Factory owns user construction. */
@FunctionalInterface
public interface StudentFactoryPort {
    StudentProfile createStudent(StudentRegistrationRequest request);
}
