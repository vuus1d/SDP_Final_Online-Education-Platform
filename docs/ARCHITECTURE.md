# Architecture

## Current structure

The implementation is split into an immutable shared domain contract, pattern-specific packages, and a facade that coordinates injected ports. `Course` and `StudentProfile` are the minimum common model agreed for this branch. They are deliberately small and should be reconciled with the teammate's Builder/Factory/Observer models before final integration.

```text
Facade client
    |
    v
EducationPlatformFacade
    |-- StudentFactoryPort ------> teammate Factory adapter (pending)
    |-- CourseBuilderPort -------> teammate Builder adapter (pending)
    |-- GradeSubsystemPort ------> grading + teammate Observer (pending)
    |-- RecommendationService --> RecommendationStrategy (runtime selectable)
    |-- CourseExtensionStorePort -> CourseComponent + Decorators
    |-- catalog / student / enrollment / quiz / progress ports
```

## Boundaries

- `domain` owns validated identifiers, course and student recommendation data, learning materials, progress, quiz result, and grade values.
- `recommendation` owns ranking only. Strategies receive a student profile and course collection; they do not read storage or mutate students/courses.
- `course` owns decorators. Decorators wrap the shared `Course` through `CourseComponent`; the immutable course reference is passed through unchanged.
- `facade.port` defines subsystem boundaries. Production adapters are supplied during team integration.
- `EducationPlatformFacade` validates resource existence and coordinates calls. It does not build users/courses, grade quiz answers, persist records, or deliver notifications itself.
- `demo` contains explicit in-memory adapters for a runnable walkthrough. They are not production infrastructure or implementations of the three teammate-owned patterns.

## Error and state handling

Invalid domain values fail at construction. Missing students/courses result in `ResourceNotFoundException`. Subsystem failures propagate to the caller. Catalog and extension persistence are separate injected ports, so the integration layer must make course creation and its base component consistent. Strategy configuration can be replaced at runtime; recommendation execution captures the currently selected strategy for each request.

## Integration flow

A caller creates a course through `CourseBuilderPort`, creates a student through `StudentFactoryPort`, and queries recommendations using `RecommendationService`. Enrollment, quiz, grade, and progress calls are routed to their respective ports. Course materials/certificate capability are composed around the same course component and persisted through `CourseExtensionStorePort`. The concrete grading subsystem must publish Observer events exactly once; the facade intentionally calls grading once and does not publish a second event.
