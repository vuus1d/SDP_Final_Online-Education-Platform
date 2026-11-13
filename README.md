# Online Education Platform

An in-memory Java 21 console application demonstrating six coordinated design patterns.

## Run

Requirements: JDK 21+ and Maven 3.9+.

```sh
mvn clean verify
mvn package
java -jar target/eduplatform-1.0-SNAPSHOT.jar
```

In IntelliJ IDEA, import this directory as a Maven project and run `com.eduplatform.Main`.
Select **12. Load Demo Data** for a quick presentation, then use the recommendation, course, quiz, notification, progress, and certificate menu actions.

## Patterns

| Pattern | Production classes |
|---|---|
| Builder | `com.eduplatform.model.Course.Builder`, `CourseCreationWizard` |
| Factory | `com.eduplatform.factory.UserFactory`, `UserService` |
| Observer | `GradeSubject`, `GradeService`, `GradeObserver`, `StudentNotificationObserver`, `NotificationService` |
| Strategy | `RecommendationStrategy`, `InterestBasedRecommendationStrategy`, `PerformanceBasedRecommendationStrategy`, `RecommendationService` |
| Decorator | `CourseComponent`, `BaseCourse`, `CourseDecorator`, `AdditionalMaterialsDecorator`, `CertificateDecorator` |
| Facade | `EducationPlatformFacade` |

The production application uses one `Course`, `Student`, and `Grade` model in `com.eduplatform.model`. Recommendation metadata (topics, availability, minimum readiness) is part of the canonical immutable Course and is supported by its Builder. Student interests and completion are stored on the canonical Student; readiness is calculated from actual saved grades. New students with no grades have 0% recorded readiness.

Course progress counts lesson positions and quizzes passed at 70% or higher (using the displayed rounded percentage). A failed attempt is saved and notified but does not count toward completion; the student may retry. A certificate requires enrollment, enabled certificate support, and 100% progress. Repeat requests return the same certificate instance/identifier. Replaying the same Grade object is rejected. Observer callback exceptions are recorded as delivery failures; they do not roll back grades or prevent later observers from running.

## Team ownership

- Student A: Builder, Factory, Observer.
- Student B: Strategy, Decorator, Facade.

The integration branch preserves both feature histories. Tests from Student B's initial independent contracts remain under test sources for compatibility; the production entry point and integrated test use only the canonical `com.eduplatform` application model.

## Tests and docs

`mvn clean verify` runs both teams' JUnit 5 tests plus `IntegratedPatternsTest` and scripted console tests. Architecture, defense notes, PlantUML sources, and a report draft are in `docs/`.

GitHub Actions runs `mvn clean verify` with Temurin Java 21 for each push and pull request. A rendered report PDF with the six pattern UML diagrams is available at `output/pdf/FINAL_REPORT_DRAFT.pdf`; authentic application screenshots are still pending and are marked BLOCKED in the report.

See [Architecture](docs/ARCHITECTURE.md), [Design Patterns](docs/DESIGN_PATTERNS.md), [Defense Guide](docs/DEFENSE_GUIDE.md), and [Report Draft](docs/FINAL_REPORT_DRAFT.md).
