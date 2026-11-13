package com.eduplatform.console;

import com.eduplatform.model.Course;
import com.eduplatform.model.DifficultyLevel;
import com.eduplatform.model.Lesson;
import com.eduplatform.model.Question;
import com.eduplatform.model.Quiz;
import com.eduplatform.model.Student;
import com.eduplatform.model.Teacher;
import com.eduplatform.model.UserRole;
import com.eduplatform.service.CourseService;
import com.eduplatform.service.NotificationService;
import com.eduplatform.service.UserService;

import java.util.List;

/** Loads a small sample data set so the full workflow can be demonstrated quickly. */
public class DemoDataLoader {

    private static final String ADMIN_EMAIL = "admin@eduplatform.com";
    private static final String TEACHER_EMAIL = "john@example.com";
    private static final String FIRST_STUDENT_EMAIL = "nurasyl@example.com";
    private static final String SECOND_STUDENT_EMAIL = "aigerim@example.com";

    private final UserService userService;
    private final CourseService courseService;
    private final NotificationService notificationService;
    private boolean loaded;

    public DemoDataLoader(UserService userService, CourseService courseService, NotificationService notificationService) {
        this.userService = userService;
        this.courseService = courseService;
        this.notificationService = notificationService;
    }

    public void load() {
        if (loaded) {
            throw new IllegalStateException("Demo data has already been loaded");
        }
        ensureDemoEmailsAreFree();
        userService.registerUser(UserRole.ADMIN, "Platform Admin", ADMIN_EMAIL);
        Teacher teacher = (Teacher) userService.registerUser(UserRole.TEACHER, "John Smith", TEACHER_EMAIL);
        Student nurasyl = (Student) userService.registerUser(UserRole.STUDENT, "Nurasyl", FIRST_STUDENT_EMAIL);
        Student aigerim = (Student) userService.registerUser(UserRole.STUDENT, "Aigerim", SECOND_STUDENT_EMAIL);
        notificationService.subscribe(nurasyl);
        notificationService.subscribe(aigerim);

        Course javaCourse = courseService.addCourse(buildJavaCourse(teacher));
        courseService.enroll(nurasyl, javaCourse);
        courseService.enroll(aigerim, javaCourse);
        nurasyl.setInterests(java.util.Set.of("java", "programming"));
        aigerim.setInterests(java.util.Set.of("java", "oop"));
        loaded = true;
    }

    private void ensureDemoEmailsAreFree() {
        for (String email : List.of(ADMIN_EMAIL, TEACHER_EMAIL, FIRST_STUDENT_EMAIL, SECOND_STUDENT_EMAIL)) {
            if (userService.isEmailRegistered(email)) {
                throw new IllegalStateException("Cannot load demo data: email '" + email + "' is already registered");
            }
        }
    }

    private Course buildJavaCourse(Teacher teacher) {
        return courseService.newCourseBuilder(teacher)
                .setTitle("Java Programming")
                .setDescription("Learn Java from basics to OOP")
                .addTopic("java").addTopic("programming").addTopic("oop")
                .addLesson(new Lesson("Introduction to Java", "JDK, JVM and your first program", 45))
                .addLesson(new Lesson("Variables and Types", "Primitive types, strings and operators", 60))
                .addLesson(new Lesson("Classes and Objects", "Encapsulation, constructors and methods", 90))
                .addQuiz(new Quiz("Java Basics Quiz", List.of(
                        new Question("Which keyword creates an object?", List.of("class", "new", "this"), 1),
                        new Question("Which type stores whole numbers?", List.of("int", "boolean", "String"), 0),
                        new Question("What runs Java bytecode?", List.of("JDK compiler", "JVM", "IDE"), 1))))
                .addQuiz(new Quiz("OOP Quiz", List.of(
                        new Question("Which principle hides internal state?", List.of("Inheritance", "Encapsulation"), 1),
                        new Question("Can a class extend two classes in Java?", List.of("Yes", "No"), 1))))
                .setDuration(30)
                .setDifficultyLevel(DifficultyLevel.BEGINNER)
                .build();
    }
}
