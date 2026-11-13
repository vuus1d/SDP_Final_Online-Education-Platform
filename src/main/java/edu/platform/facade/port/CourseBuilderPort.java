package edu.platform.facade.port;

import edu.platform.domain.Course;
import edu.platform.domain.CourseDraft;

/** Adapter to the team's Builder; the concrete Builder owns course construction. */
@FunctionalInterface
public interface CourseBuilderPort {
    Course build(CourseDraft draft);
}
