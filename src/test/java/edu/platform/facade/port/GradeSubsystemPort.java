package edu.platform.facade.port;

import edu.platform.domain.Grade;

/** Grade subsystem adapter; its Observer integration is responsible for delivery and deduplication. */
@FunctionalInterface
public interface GradeSubsystemPort {
    Grade assign(Grade grade);
}
