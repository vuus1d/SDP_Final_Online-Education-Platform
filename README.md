# Online Education Platform

A Java 21 Maven project for an Online Education Platform. This feature branch implements **Strategy**, **Decorator**, and **Facade**, with explicit ports for the teammate-owned **Builder**, **Factory**, and **Observer** implementations.

## Project structure

- `domain` — small immutable shared contracts used by the three patterns.
- `recommendation` — interchangeable recommendation strategies and runtime context.
- `course` — course component and composable decorators.
- `facade` — platform API and injected subsystem ports.
- `demo` — runnable demonstration of this branch's patterns using clearly labelled in-memory demo adapters.
- `src/test` — JUnit 5 behavior tests, including facade integration-oriented tests with test doubles.
- `docs` — architecture, pattern notes, team contract, defense guide, report draft, and PlantUML source.

## Build and run

Requirements: JDK 21 or newer and Maven 3.9 or newer. The POM compiles against Java 21.

```sh
mvn clean verify
java -cp target/classes edu.platform.demo.DemoApplication
```

After `mvn clean verify`, run `java -cp target/classes edu.platform.demo.DemoApplication`. It demonstrates strategy switching, course decoration, quiz/grade delegation, and explicit certificate eligibility. Its local ports stand in for still-pending team components; it does not demonstrate the Builder, Factory, or Observer patterns.

## Patterns

| Pattern | Responsibility | Implementation |
|---|---|---|
| Strategy | Rank eligible courses by student interests or performance, switch at runtime | `RecommendationStrategy`, `InterestBasedRecommendationStrategy`, `PerformanceBasedRecommendationStrategy`, `RecommendationService` |
| Decorator | Add materials and explicit certificate capability while preserving the shared course | `CourseComponent`, `CourseDecorator`, `AdditionalMaterialsDecorator`, `CertificateDecorator` |
| Facade | Coordinate registration, course creation, enrollment, recommendations, quiz, grades, progress, and extensions | `EducationPlatformFacade` |
| Builder | Teammate responsibility; adapter contract only | `CourseBuilderPort` |
| Factory | Teammate responsibility; adapter contract only | `StudentFactoryPort` |
| Observer | Teammate responsibility; notification remains inside grade subsystem | `GradeSubsystemPort` integration contract |

## Branch responsibilities and integration status

- Teammate branch: `feature/builder-factory-observer` — Builder, Factory, Observer.
- This branch: `feature/strategy-decorator-facade` — Strategy, Decorator, Facade.
- The shared remote base/default branch was `feature/builder-factory-observer` at `2523741` when this worktree was created.
- No teammate implementation was present in the base commit used for this work. Ports and a minimum shared model are documented in [TEAM_INTEGRATION_CONTRACT.md](docs/TEAM_INTEGRATION_CONTRACT.md).
- Full six-pattern integration remains pending until the teammate code is merged and wired through production adapters.

## Tests

Run `mvn clean verify`. Tests cover both strategies, switching, filtering and deterministic ordering, decorator composition and certificate eligibility, and facade delegation/error handling with test doubles. In the current environment, Maven 3.9.9 completed `clean verify` successfully: 15 tests, 0 failures, 0 errors, 0 skipped. It used a temporary Maven cache under `/private/tmp` because the system cache is read-only.

## Known limitations

This is an in-memory academic application core: there is no persistence, authentication, UI, or actual quiz/grade implementation in this branch. The facade ports are integration contracts, not substitutes for the teammate's patterns. The demo uses local adapters for execution only. Certificate requests create an in-memory certificate value when the student has 100% course progress; persistent issuance and duplicate-certificate policy require a real certificate subsystem.
