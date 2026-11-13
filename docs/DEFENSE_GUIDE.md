# Defense Guide (simple English, B1)

## What is Strategy?

Strategy puts different algorithms behind one interface. In our project, the system can recommend courses by student interests or by academic performance. The application can change the algorithm while it is running.

## What is Decorator?

Decorator adds features by wrapping an object. Our course can receive extra learning materials and certificate support. The original course stays the same, and the features can be combined in different orders.

## What is Facade?

Facade gives the application one simple entry point. Our `EducationPlatformFacade` coordinates services for students, courses, recommendations, quizzes, grades, and progress. It sends work to those services.

## Why did we use these patterns?

Strategy lets us add a recommendation algorithm without changing the recommendation service. Decorator lets us add optional course features without making many course subclasses. Facade makes common platform actions easier to call and test.

## Where are they implemented?

Strategy is in `edu.platform.recommendation`. Decorator is in `edu.platform.course`. Facade is in `edu.platform.facade`.

## How do they work together?

The facade gets courses and a student profile. It asks the recommendation service for a ranked list. The service uses the selected strategy. The facade can also wrap a course with materials or certificate support.

## How can we demonstrate them?

Run `edu.platform.demo.DemoApplication`. First, it prints recommendations by interests. Then it changes to performance recommendations. It adds materials to a course and shows certificate support. A certificate is returned only when course progress is complete. The demo uses temporary local adapters for other subsystems; Builder, Factory, and Observer still need the teammate's implementation and production integration.
