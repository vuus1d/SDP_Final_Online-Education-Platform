# Architecture

`com.eduplatform.Main` assembles in-memory services and a single `EducationPlatformFacade`, then starts `ConsoleMenu`. Interactive registration, course creation, enrollment, quiz completion, recommendations, course extensions, progress, and certificate requests use the facade. The menu retains the teammate's admin views, notification inbox and demo data loader.

## Canonical model and flow

- `model.Course` is the only production course entity. Its validated `Builder` collects teacher, description, lessons, quizzes, duration, difficulty, topics, readiness threshold and availability.
- `model.Student` is the only production student entity. It owns enrollment, interests, notification inbox and completed lesson positions/quiz identities. Lesson titles are presentation labels, not completion keys.
- `model.Grade` is the only production grade entity. `QuizService` calculates it and `GradeService` saves and publishes it.
- A quiz passes when the rounded grade percentage is at least 70%. A failed attempt is saved and notified but does not count as completed; retrying is allowed.
- `GradeService` commits a Grade before publishing. Runtime exceptions from observers are captured as delivery failures and later observers are still called. `QuizService` can then finish its state update without losing the grade.
- `CourseService` owns the registered catalog and enrollment checks; `UserService` registers users through `UserFactory`.
- `RecommendationService` builds strategy results from canonical Courses and a recommendation projection containing a registered Student and average saved grade. No-grade students start at 0% readiness.
- `CourseComponent` wraps the canonical Course. Decorators add materials and certificate capability without changing the wrapped entity.
- `EducationPlatformFacade` validates cross-service identifiers and delegates rules to the relevant service.

## End-to-end

`ConsoleMenu → EducationPlatformFacade → UserFactory/UserService → Course.Builder/CourseService → RecommendationService → CourseComponent decorators → QuizService → GradeService → observers/notifications`. Progress reads completion marks from Student. Certificate requests require enrollment, all lesson positions and passed quizzes complete, and enabled certificate capability.

## Runtime and tests

The application is in-memory; restarting the process clears users, courses, grades and certificates. `IntegratedPatternsTest` drives real services and all six patterns. Existing tests from both branches remain in the Maven test source set. `ConsoleMenuTest` exercises the actual menu with scripted input.
