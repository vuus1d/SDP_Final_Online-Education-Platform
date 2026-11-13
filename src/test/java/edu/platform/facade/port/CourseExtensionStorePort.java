package edu.platform.facade.port;

import edu.platform.course.CourseComponent;
import edu.platform.domain.CourseId;
import java.util.Optional;

public interface CourseExtensionStorePort {
    Optional<CourseComponent> find(CourseId id);
    void save(CourseComponent course);
}
