# Online Education Platform - Final Report Draft

## Introduction

This team project demonstrates six design patterns in a Java console platform for online courses. Student A implemented Builder, Factory and Observer. Student B implemented Strategy, Decorator and Facade. The integration uses one production Course, Student and Grade model.

## Body

The Factory creates users by role. The Course Builder assembles and validates a course with lessons and quizzes. Strategy recommends courses using student interests or the student's average saved grade. Decorator adds materials and certificate support while keeping the course's lessons, quizzes and teacher. Facade provides one application API over user, course, quiz, grade and notification services. After a quiz is graded, Observer sends a notification to the matching student.

Progress counts completed lessons and quizzes. A certificate is issued only when the student is enrolled, certificate support is enabled, and progress is complete. A repeated certificate request returns the previously issued certificate.

The main classes are `src/main/java/com/eduplatform/Main.java`, `model/Course.java`, `factory/UserFactory.java`, `observer/GradeService.java`, `recommendation/RecommendationService.java`, `decorator/CourseComponent.java`, and `facade/EducationPlatformFacade.java`. See `docs/uml/` for source UML diagrams.

### Verification evidence

`mvn clean verify` passed on 2026-11-13: 68 tests, 0 failures, 0 errors and 0 skipped. This includes the production-service integration scenario and a scripted full console workflow. `mvn package` passed, and `java -jar target/eduplatform-1.0-SNAPSHOT.jar` displayed the unified menu and exited successfully. Compilation targets Java 21 (`maven.compiler.release=21`); the available runtime used for verification was OpenJDK 26.0.2.1.

### Screenshots

Pending: capture authentic screenshots from the running console application and add them to the final report. No screenshots are fabricated in this draft.

## Conclusion

The patterns cooperate through one in-memory application and one console entry point. The code separates creation, recommendation, course extensions, grading notifications and orchestration.

## Further Work

Add persistent storage, authentication, richer lesson completion records, a web interface, and certificate export. Add a PDF report with actual screenshots and attach a ZIP of source code for submission.

## Export

Render this Markdown with a Markdown-to-PDF tool (for example, Pandoc) after adding screenshots. Create a source archive from the repository root with `git archive --format=zip --output=eduplatform-source.zip HEAD`. Include the report PDF separately in the final submission.
