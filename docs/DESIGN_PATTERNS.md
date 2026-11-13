# Design Patterns

## Builder

`com.eduplatform.model.Course.Builder` assembles an immutable Course by method chaining. Its `build()` validates required fields, lesson presence, duration, difficulty and duplicate quiz titles. Recommendation metadata is configured with `addTopic`, `setMinimumPerformance` and `setAvailable`.

## Factory

`com.eduplatform.factory.UserFactory` creates Student, Teacher or Admin from a `UserRole`; `UserService` uses it for every registration and checks email uniqueness.

## Observer

`GradeService` implements `GradeSubject` and stores validated grades before publishing them once. `StudentNotificationObserver` listens for grade events and sends a notification only to its bound student. `NotificationService` handles subscription and unsubscription.

## Strategy

`RecommendationStrategy` selects a ranking algorithm. `InterestBasedRecommendationStrategy` compares real Student interests with Course topics. `PerformanceBasedRecommendationStrategy` compares the average of saved Grade values with a course readiness threshold. `RecommendationService.setStrategy` switches the algorithm while the app is running. Both strategies exclude unavailable and completed courses.

## Decorator

`CourseComponent` exposes a canonical Course plus optional materials and certificate capability. `BaseCourse` is the undecorated component. `AdditionalMaterialsDecorator` and `CertificateDecorator` can be nested in either order. They preserve the Course's teacher, lessons and quizzes and do not mutate the Course. Certificates are requested explicitly and require enrollment plus 100% real activity progress.

## Facade

`EducationPlatformFacade` validates cross-service resources and offers one API for registration, course creation, enrollment, recommendations, course extensions, quiz submission, grade and notification views, progress and certificates. Each rule remains in `UserService`, `CourseService`, `QuizService`, `GradeService`, `NotificationService`, Strategy or Decorator. The menu uses this API for integrated actions.

## Coordination

Factory creates the Student and Teacher. Builder creates a Course registered by CourseService. Strategy reads the same Course and Student data. Decorator wraps that Course. Facade coordinates enrollment and quiz submission. QuizService records a Grade once; Observer notifies the correct Student. Student activity state drives progress and certificate eligibility.
