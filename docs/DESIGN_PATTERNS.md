# Design Patterns

This document describes the three patterns implemented on `feature/strategy-decorator-facade`. The teammate-owned patterns are represented only by integration contracts and are not claimed as implemented here.

## Strategy

**Intent.** Make recommendation algorithms interchangeable behind one stable API.

**Problem.** Interest matching and academic readiness use different rules. A conditional-heavy recommender would couple the service to every algorithm and make new algorithms require edits to that service.

**Solution.** `RecommendationStrategy` accepts a student profile, course collection, and result limit. `InterestBasedRecommendationStrategy` ranks by topic overlap. `PerformanceBasedRecommendationStrategy` filters courses above the student's readiness score and ranks the remaining courses by closeness of challenge. `RecommendationService.setStrategy` replaces the active strategy at runtime. Both algorithms filter unavailable and already completed courses, use stable score/title/id ordering, and return an empty list when no course qualifies.

**Participants and files.** `RecommendationStrategy`, `InterestBasedRecommendationStrategy`, `PerformanceBasedRecommendationStrategy`, `RecommendationService`, and `CourseRecommendation` under `src/main/java/edu/platform/recommendation`; input records live under `domain`.

**Interaction.** `EducationPlatformFacade.getRecommendedCourses` loads a student and available catalog entries and delegates to the service. The facade does not know how ranking works.

**Benefits and trade-offs.** New algorithms can be added as implementations without modifying existing ones. Runtime switching is explicit and testable. Ranking scores are intentionally simple teaching rules; calibration and richer grade history belong to future work.

**Defense demo.** Run `DemoApplication`; it asks the same profile for recommendations, switches from interest to performance strategy, and prints the new result. Tests verify each strategy and runtime switching.

## Decorator

**Intent.** Add optional course behavior by wrapping a course component rather than adding feature flags to `Course`.

**Problem.** Some courses provide extra materials or certificate capability while the base course model should remain stable and untouched. Independent options should compose in either order.

**Solution.** `CourseComponent` is the component contract. `BaseCourse` adapts the shared immutable `Course`. `CourseDecorator` delegates unchanged operations. `AdditionalMaterialsDecorator` appends immutable material values. `CertificateDecorator` enables a certificate request; it issues only on an explicit request for matching course progress at 100%. Wrapping the course does not mutate it and every decorator returns the identical base `Course` reference.

**Participants and files.** `CourseComponent`, `BaseCourse`, `CourseDecorator`, `AdditionalMaterialsDecorator`, and `CertificateDecorator` under `src/main/java/edu/platform/course`; value types under `domain`.

**Interaction.** `EducationPlatformFacade` retrieves the existing decorated component (or creates `BaseCourse`) and saves the newly wrapped component through `CourseExtensionStorePort`. Certificate request first obtains progress from the progress port.

**Benefits and trade-offs.** Features compose without a subclass for every combination and preserve course identity. Nested wrappers mean callers should use the component API consistently. Certificate persistence and duplicate-issuance policy are not included.

**Defense demo.** Add materials, wrap with certificate capability, show the original title and material list, then request a certificate with complete and incomplete progress.

## Facade

**Intent.** Offer one understandable application API over platform subsystems.

**Problem.** A client otherwise coordinates registration, catalog lookup, recommendation, enrollment, course extension, quizzes, grades, and progress itself.

**Solution.** `EducationPlatformFacade` receives focused interfaces through its constructor, validates IDs and resource existence, and delegates. Course creation is handed to `CourseBuilderPort`; registration to `StudentFactoryPort`; recommendations to `RecommendationService`; grades to `GradeSubsystemPort`, where Observer delivery belongs. The facade does not duplicate those subsystems' business rules.

**Participants and files.** `EducationPlatformFacade`, `ResourceNotFoundException`, and the interfaces under `facade/port`.

**Interaction.** The facade composes Strategy and Decorator directly. Builder, Factory, and Observer are expected through their ports once the teammate's code is integrated.

**Benefits and trade-offs.** Callers need fewer dependencies and subsystem calls are easy to test with doubles. The facade has several constructor dependencies; this reflects the currently requested use cases and can be divided into smaller application services if the scope grows.

**Defense demo.** Construct the facade with local demo adapters and call registration, course creation, recommendations, enrollment, material addition, and progress-checked certificate request. The demo labels the Builder/Factory/Observer ports as pending and does not claim those patterns.
