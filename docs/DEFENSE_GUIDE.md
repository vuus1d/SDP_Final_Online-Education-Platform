# Defense Guide (simple B1 English)

## What is each pattern, why did we choose it, and where is it?

### Builder

Builder creates a complex object step by step. We use it because a course has many optional and required parts. `Course.Builder` is inside `com.eduplatform.model.Course`. It adds lessons, quizzes, teacher, duration, topics and difficulty. `build()` checks the data before it returns a Course. We can show `CourseBuilderTest` or create a course in the menu.

Advantage: the creation steps are clear and validation is in one place. Disadvantage: the Builder needs methods for new Course fields.

### Factory

Factory chooses which object to create. We use it to create users by role. `UserFactory` creates Student, Teacher or Admin. `UserService` calls it during registration. We can register each role or run `UserFactoryTest`.

Advantage: callers do not construct role classes. Disadvantage: a new role needs a new factory case.

### Observer

Observer sends an event to subscribers. We use it to notify a student after a grade is saved. `GradeService` is the subject. `StudentNotificationObserver` is an observer. `NotificationService` subscribes and unsubscribes it. We can complete a quiz and open the notification menu.

Advantage: grading does not need to know notification details. Disadvantage: the observer must filter events for its student.

### Strategy

Strategy lets us change an algorithm at run time. We use it for two course recommendation methods. `InterestBasedRecommendationStrategy` matches interests and topics. `PerformanceBasedRecommendationStrategy` checks the average of real saved grades. `RecommendationService` changes the active strategy. We can set student interests and switch the algorithm in the menu.

Advantage: a new ranking method can be added separately. Disadvantage: users need a way to choose the method.

### Decorator

Decorator adds features around an object. We use it to add materials and certificate support to a course. `BaseCourse`, `CourseDecorator`, `AdditionalMaterialsDecorator`, and `CertificateDecorator` are in `com.eduplatform.decorator`. We can add materials, enable certificates, finish every lesson and quiz, then request a certificate.

Advantage: features can be combined without changing Course. Disadvantage: many nested features can be harder to inspect.

### Facade

Facade gives one simple API for several services. We use it so the menu can call one application API. `EducationPlatformFacade` coordinates user, course, quiz, grade, notification, strategy and decorator services. We can follow `IntegratedPatternsTest` or use the console menu.

Advantage: the menu has a simple interface. Disadvantage: the facade must delegate and must not contain all business rules.

## How do the patterns work together?

Factory creates a Student and Teacher. Builder creates a Course. Strategy recommends that Course to a Student. Facade enrolls the Student. Decorator adds materials and certificate support. QuizService saves a grade. Observer sends a notification. The same Student activity data controls progress and certificate eligibility.

## Team responsibilities

- **Student A:** Builder, Factory, Observer.
- **Student B:** Strategy, Decorator, Facade.

## Advantages and limitations

The patterns keep object creation, choices, notifications and course extensions in separate classes. They work together in one console app. The project stores data in memory, so data is lost when the app stops. A future version can add a database and a web interface.
