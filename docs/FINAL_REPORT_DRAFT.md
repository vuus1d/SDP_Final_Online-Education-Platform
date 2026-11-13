# Online Education Platform — Final Report

## Introduction

This team project is an in-memory Java 21 console application for course registration, learning progress, quizzes, grades, notifications, recommendations, and certificates. It demonstrates six GoF patterns in one application. Student A is responsible for Builder, Factory, and Observer; Student B is responsible for Strategy, Decorator, and Facade. The integrated production workflow uses one Course, Student, and Grade model.

## Body

### Architecture and patterns

`com.eduplatform.Main` creates the application services and starts `ConsoleMenu`. User scenarios are sent through `EducationPlatformFacade`. Business rules remain in model classes and services.

- **Builder:** `Course.Builder` assembles a validated course with teacher, description, lessons, quizzes, duration, difficulty, topics, readiness threshold, and availability.
- **Factory:** `UserFactory` creates Student, Teacher, or Admin after role and input validation.
- **Observer:** `GradeService` stores a grade once and notifies subscribers. `StudentNotificationObserver` delivers the notification to its student. A callback failure is recorded and does not stop other subscribers.
- **Strategy:** `RecommendationService` switches at runtime between interest matching and performance matching. Performance is calculated from saved grades; a new student starts at 0% recorded performance.
- **Decorator:** `BaseCourse` and nested decorators add extra materials and certificate support without changing the canonical Course. Certificate requests are explicit and require enrollment and 100% progress.
- **Facade:** `EducationPlatformFacade` presents a single API over user, course, quiz, grade, notification, recommendation, and decorated-course operations.

### Quiz and progress rules

A quiz attempt is passing when its rounded displayed percentage is at least 70%. Every valid attempt is saved as a Grade and sent to observers. Only passing attempts count as completed quiz activities; a failed attempt can be retried. Lesson completion is stored by the lesson's zero-based position within its course, so two lessons with the same title remain distinct. Progress is the ratio of completed lesson positions and passed quizzes to all lessons and quizzes. Empty courses have 0% progress. A certificate is issued only after an explicit request, when the student is enrolled, certificate support is enabled, and progress is 100%. Repeated requests return the already-issued certificate.

### Java examples

The Builder creates one Course while retaining all lesson and quiz data:

```java
Course course = Course.builder()
    .setId("CRS-101")
    .setTitle("Java Basics")
    .setTeacher(teacher)
    .setDuration(10)
    .setDifficultyLevel(DifficultyLevel.BEGINNER)
    .addTopic("java")
    .addLesson(lesson)
    .addQuiz(quiz)
    .setMinimumPerformance(60)
    .setAvailable(true)
    .build();
```

QuizService persists and publishes each grade before updating completion from the pass rule:

```java
Grade grade = new Grade(student, course, quiz, correctAnswers, LocalDateTime.now());
gradeService.recordGrade(grade);
student.markQuizCompleted(course, quiz, grade);
```

### Verification

On 2026-11-13, `mvn clean verify` completed successfully: 73 JUnit tests, 0 failures, 0 errors, and 0 skipped. The suite includes a scripted end-to-end console scenario and regressions for duplicate lesson titles, failed quiz completion, atomic interest replacement, and observer callback exceptions. `mvn package` also succeeded. The packaged executable JAR was run with scripted input (`12`, `0`); it loaded demo data, displayed the unified menu, exited with code 0, and printed `Goodbye!`. Maven compiled for release 21; the installed runtime used for these checks was OpenJDK 26.0.2.1.

### UML diagrams

The six pattern diagrams plus the overall architecture and end-to-end sequence are rendered from the PlantUML sources in `docs/uml/`. See the images following this report section in the PDF.

### Application screenshots — BLOCKED

No authentic application screenshot could be saved in this environment. IntelliJ IDEA was available, but the exposed UI session did not provide a way to export a screenshot of the Java console to the repository. No screenshots are fabricated or represented by generated artwork. Capture screenshots from a local IntelliJ run before submitting the report.

## Conclusion

The six patterns cooperate through the same domain state and the same console application. The automated test suite verifies the integrated flow from Factory registration and Builder course creation through recommendations, enrollment, quiz grading, observer notification, real progress, and certificate eligibility. The source and UML agree with the integrated class structure.

## Further Work

The application currently stores data in memory and has no authentication or persistent storage. Future work can add a database, user authentication, a web interface, certificate export, and recovery of saved learning progress. The report still needs genuine application screenshots before final academic submission.

## Export and package commands

Run `python3 docs/export_report.py` to regenerate the PDF after PlantUML images exist in `docs/uml/rendered/`. Create a source archive with `git archive --format=zip --output=eduplatform-source.zip HEAD`; include the final PDF alongside the archive for submission.
