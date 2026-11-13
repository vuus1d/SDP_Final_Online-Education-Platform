# Team Integration Contract

## Ownership

- Student A (`feature/builder-factory-observer`): Builder, Factory, Observer.
- Student B (`feature/strategy-decorator-facade`): Strategy, Decorator, Facade.

## Canonical integration choices

- Production packages are rooted at `com.eduplatform`.
- `com.eduplatform.model.Course`, `Student`, and `Grade` are the only production entities for those concepts.
- Course recommendation fields are part of Course and its Builder: topics, minimum performance and availability.
- Student interests and completed lesson/quiz IDs live on Student. Performance is calculated from saved Grade values; no-grade readiness is 0%.
- `EducationPlatformFacade` receives the real User, Course, Quiz, Grade and Notification services, plus the real RecommendationService.
- Course decorators wrap a canonical Course. Certificate request is explicit, requires enrollment and complete activity state, and repeated requests return the same issued value.
- QuizService is the only production path from quiz answers to grade recording. GradeService stores and publishes each Grade event once. A new quiz attempt is a new event; the same Grade object cannot be re-published.
- `Main` is the sole application entry point. The production console workflow calls the Facade for combined user actions.

## Test compatibility

Both branches' original JUnit checks remain in the test source set. The previous Student B contract-only demo sources are kept as test fixtures; production code uses the integrated `com.eduplatform` implementations. `IntegratedPatternsTest` verifies the real production services and all six patterns together.
