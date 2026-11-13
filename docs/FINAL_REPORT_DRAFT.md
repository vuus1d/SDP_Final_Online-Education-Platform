# Online Education Platform — Final Report Draft

> Draft status: this branch implements Strategy, Decorator, and Facade. Builder, Factory, Observer, full team integration, screenshots, and final PDF export remain pending. Do not present this as a completed six-pattern system until the pending sections are replaced with verified team evidence.

## 1. Introduction

The project explores object-oriented design patterns in an Online Education Platform. The team plan combines six patterns: Builder for course construction, Factory for user creation, Observer for grade notifications, Strategy for course recommendations, Decorator for optional course features, and Facade for simplified application operations.

The work in this branch focuses on Strategy, Decorator, and Facade. Its goal is to make recommendation rules interchangeable, extend courses without changing their core model, and give clients one coordinated API. The current integration uses shared domain contracts and ports for the components being implemented on the teammate's branch.

## 2. Body

### 2.1 Architecture

The code separates immutable domain values, recommendation algorithms, course decorators, and facade ports. The facade receives dependencies through its constructor. It checks for missing students and courses, then delegates domain operations. It does not implement user/course construction, grade evaluation, persistence, or notification delivery.

**Insert:** Overall architecture diagram from `docs/uml/overall-architecture.puml`.

### 2.2 Builder — pending teammate implementation

**Pending evidence:** real Builder classes, construction steps, Java code excerpt, tests, UML participant names, and integration result. Current branch has only `CourseBuilderPort` and `CourseDraft` as integration contracts.

### 2.3 Factory — pending teammate implementation

**Pending evidence:** real Factory classes for Student, Teacher, and Admin, Java code excerpt, tests, UML participant names, and integration result. Current branch has only `StudentFactoryPort` and a student registration request contract.

### 2.4 Observer — pending teammate implementation

**Pending evidence:** actual subject/observer classes, event flow, Java code excerpt, tests, and proof that notifications are delivered once. `GradeSubsystemPort` marks the integration boundary; this branch does not implement Observer.

### 2.5 Strategy — implemented on this branch

`RecommendationStrategy` defines the common recommendation operation. The interest strategy calculates topic overlap. The performance strategy recommends only courses for which the student's performance meets the course readiness threshold and ranks courses by challenge fit. Both ignore unavailable and already completed courses. `RecommendationService` can switch implementations at runtime. Stable tie-breaking by normalized title and course ID makes repeated results deterministic.

**Java references:** `src/main/java/edu/platform/recommendation/`.

**Insert:** `docs/uml/strategy-class.puml`.

### 2.6 Decorator — implemented on this branch

`BaseCourse` adapts the shared immutable course to `CourseComponent`. `AdditionalMaterialsDecorator` adds material values. `CertificateDecorator` enables a certificate request, but does not issue one automatically. An explicit request returns a certificate only for 100% progress on the same course. Decorators can be nested in either order and return the original course object.

**Java references:** `src/main/java/edu/platform/course/`.

**Insert:** `docs/uml/decorator-class.puml`.

### 2.7 Facade — implemented with integration ports

`EducationPlatformFacade` coordinates registration, course creation, recommendations, enrollment, course extensions, quizzes, grading, progress, and certificate requests. It delegates to injected interfaces. The grading subsystem owns Observer notification; the facade calls it once and does not send a second notification.

**Java references:** `src/main/java/edu/platform/facade/`.

**Insert:** `docs/uml/facade-class.puml`.

### 2.8 Testing and demonstration

JUnit 5 tests cover recommendation ranking, filtering, switching, stable behavior, decorator composition and certificate eligibility, and facade delegation and error handling. Facade tests use test doubles because production team adapters are not present in this branch.

**Verified test command and output:** `/private/tmp/apache-maven-3.9.9/bin/mvn -Dmaven.repo.local=/private/tmp/maven-repository clean verify` completed with `BUILD SUCCESS`; 15 tests ran, with 0 failures, 0 errors, and 0 skipped. Maven 3.9.9 compiled the project with `--release 21` on the available JDK 26. The absolute paths are specific to this verification environment.

`DemoApplication` runs a local walkthrough for this branch's three patterns. Its Builder/Factory/Observer port implementations are demo adapters and are not evidence of the teammate patterns.

**Screenshots:** pending. Capture genuine screenshots only after the integrated application is running.

### 2.9 UML and source code

The reproducible PlantUML source files are in `docs/uml/`. Export diagrams after the final class structure is agreed. Include relevant Java code excerpts from all six actual implementations in the final report.

## 3. Conclusion

This branch provides a tested design direction for interchangeable recommendations, composable course features, and a coordinating facade. Its interfaces are ready for team integration, but the six-pattern project cannot be called complete until Builder, Factory, and Observer are integrated and the complete workflow is verified.

## 4. Further Work

1. Reconcile the shared domain model with the teammate's classes.
2. Connect real Builder, Factory, Observer, catalog, enrollment, quiz, and progress implementations.
3. Add integration tests for all six patterns and notification delivery.
4. Run and record the full verification command on the merged project.
5. Capture genuine application screenshots and replace pending UML labels with actual class names.
6. Export the approved report to PDF and package the verified source as ZIP.
