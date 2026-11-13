# EduPlatform

EduPlatform is an online education platform with a console interface, written in Java.
Students enroll in courses, study lessons and complete quizzes. Teachers create courses. Students are notified about their grades.

The project demonstrates design patterns as a team assignment.
This branch contains **Builder** (course creation), **Factory** (user creation) and **Observer** (grade notifications).
See [docs/DESIGN_PATTERNS.md](docs/DESIGN_PATTERNS.md) and [docs/TEAMMATE_INTEGRATION.md](docs/TEAMMATE_INTEGRATION.md).

## Requirements

- JDK 17 or newer
- Maven 3.9+ (IntelliJ IDEA bundles one)

## Build, test and run

```bash
mvn test                                  # run all tests
mvn package                               # build target/eduplatform-1.0-SNAPSHOT.jar
java -jar target/eduplatform-1.0-SNAPSHOT.jar
```

In IntelliJ IDEA, open the project as a Maven project and run `com.eduplatform.Main`.

For a quick demonstration, choose **12. Load Demo Data** first.

## Running the demonstrations

| Entry point | Shows |
|---|---|
| `com.eduplatform.Main` | Integrated interactive application: Factory + Builder + Observer in one workflow |
| `com.eduplatform.builder.BuilderDemo` | Builds a course with `Course.Builder` (lessons, quizzes), prints it, shows validation |
| `com.eduplatform.factory.FactoryDemo` | Creates Student, Teacher and Admin with `UserFactory`, shows types and invalid input handling |
| `com.eduplatform.observer.ObserverDemo` | Subscribes student observers, publishes grades, shows notification delivery and unsubscribe |

In IntelliJ IDEA, click the green Run icon next to the `main` method of any of these classes.
From the command line after `mvn package`, use for example `java -cp target/classes com.eduplatform.observer.ObserverDemo`.

## UML

PlantUML sources are in [docs/uml](docs/uml):

- `Builder_UML.puml`: Builder pattern (course creation)
- `Factory_UML.puml`: Factory pattern (user creation)
- `Observer_UML.puml`: Observer pattern (grade notifications)
- `eduplatform-architecture.puml`: overall architecture
