# Team Integration Contract

## Status

At branch creation, the remote default branch `feature/builder-factory-observer` pointed to `2523741 Initial project setup`. That commit contained no Java source or build file. This branch therefore provides explicit contracts and a minimal shared model; it does **not** contain the teammate's Builder, Factory, or Observer implementations.

## Shared domain types introduced here

- `edu.platform.domain.CourseId(String value)` — nonblank, trimmed course identifier.
- `edu.platform.domain.StudentId(String value)` — nonblank, trimmed student identifier.
- `edu.platform.domain.Course(CourseId id, String title, String description, Set<String> topics, int minimumPerformance, boolean available)` — immutable shared course projection; topic values are normalized to lowercase.
- `edu.platform.domain.StudentProfile(StudentId id, Set<String> interests, double performanceScore, Set<CourseId> completedCourseIds)` — recommendation projection; performance is in `[0,100]`, interests are normalized to lowercase.
- `CourseDraft` and `StudentRegistrationRequest` — facade inputs for construction and registration.

If the teammate already has equivalent domain classes in their working tree, agree on one canonical package/type and replace the narrow adapters at integration. Do not keep parallel Course/Student models. Course needs stable identity, topics, minimum readiness/performance, availability, and description/title; the recommender only needs the fields on `Course` and `StudentProfile` above.

## Ports and adapter responsibilities

| Contract | Required behavior | Teammate integration point |
|---|---|---|
| `CourseBuilderPort.build(CourseDraft)` | Return a non-null Course with the requested ID or facade fails fast | Adapter delegates to Builder and maps its result to shared Course |
| `StudentFactoryPort.createStudent(StudentRegistrationRequest)` | Return a non-null StudentProfile with requested ID | Adapter delegates to Factory; created user must be findable through `StudentDirectoryPort` |
| `GradeSubsystemPort.assign(Grade)` | Persist/assign grade and return it, or propagate failure | Adapter delegates to grading subsystem; Observer publication is owned here and occurs once |
| `CourseCatalogPort` | Find, list available, save shared courses | Connect to course repository/catalog |
| `StudentDirectoryPort` | Look up recommendation profile by ID | Connect to Factory/user repository projection |
| `EnrollmentPort` | Perform enrollment and report domain failures | Connect to enrollment subsystem |
| `CourseExtensionStorePort` | Store/retrieve `CourseComponent` by its underlying Course ID | Connect to application persistence or in-memory store |
| `QuizSubsystemPort` | Submit answers and return `QuizResult` | Connect to quiz subsystem |
| `ProgressQueryPort` | Return progress for the requested student and course | Connect to learning progress subsystem |

All ports are under `src/main/java/edu/platform/facade/port`. Ports intentionally do not create another Builder, Factory, or Observer implementation.

## Facade operations

`EducationPlatformFacade` exposes `registerStudent`, `createCourse`, `getRecommendedCourses`, `enrollStudent`, `addCourseMaterials`, `enableCourseCertificate`, `submitQuiz`, `assignGrade`, `getStudentProgress`, and `requestCertificate`. It performs existence checks and basic coordination. Business rules/errors from injected subsystems propagate. A missing student/course yields `ResourceNotFoundException`.

For `createCourse`, Builder adapter output is saved to catalog and a `BaseCourse` is stored in the extension store. Registration adapter output must be available from the student directory. Grade facade delegation occurs once; do not add a separate facade notification.

## Reconciliation checklist when branches are combined

1. Compare existing model classes with `Course`, `StudentProfile`, IDs, `CourseDraft`, and `StudentRegistrationRequest`; choose one canonical model.
2. Implement production adapters for Builder, Factory, Observer/grading, catalog, directory, enrollment, quiz, and progress.
3. Verify Observer notification is triggered once per successful grade assignment and not on failure.
4. Wire a production composition root; the current `DemoApplication` adapters are demonstration-only.
5. Add integration tests against the real adapters and run `mvn clean verify`.
6. Complete the full six-pattern scenario, then update the report and capture genuine screenshots.

## Future merge process

Do not merge this branch into the teammate branch until the shared model and adapters are reviewed. Once both branches are ready, open a pull request from `feature/strategy-decorator-facade` into the agreed team base (`feature/builder-factory-observer` if that remains the repository default). Resolve any domain-model conflicts by keeping one canonical `Course`/`Student` model, then run `mvn clean verify` on the combined branch. The project owner merges the reviewed pull request normally; do not force-push or rewrite either branch. After merge, add the full six-pattern integration demonstration and update the report evidence.
