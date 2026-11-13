package com.eduplatform.console;

import com.eduplatform.model.Admin;
import com.eduplatform.model.Course;
import com.eduplatform.model.Grade;
import com.eduplatform.model.Lesson;
import com.eduplatform.model.Notification;
import com.eduplatform.model.Question;
import com.eduplatform.model.Quiz;
import com.eduplatform.model.Student;
import com.eduplatform.model.User;
import com.eduplatform.model.UserRole;
import com.eduplatform.observer.GradeService;
import com.eduplatform.service.CourseService;
import com.eduplatform.service.NotificationService;
import com.eduplatform.service.QuizService;
import com.eduplatform.service.UserService;
import com.eduplatform.facade.EducationPlatformFacade;
import com.eduplatform.recommendation.*;
import com.eduplatform.model.LearningMaterial;
import com.eduplatform.model.CourseProgress;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/** Main console menu. Each option delegates the actual work to a service. */
public class ConsoleMenu {

    private static final int EXIT_OPTION = 0;
    private static final String SEPARATOR = "================================";

    private final ConsoleInput input;
    private final PrintStream out;
    private final UserService userService;
    private final CourseService courseService;
    private final QuizService quizService;
    private final GradeService gradeService;
    private final NotificationService notificationService;
    private final CourseCreationWizard courseCreationWizard;
    private final DemoDataLoader demoDataLoader;
    private final EducationPlatformFacade facade;
    private final Map<Integer, MenuItem> menuItems = new LinkedHashMap<>();

    public ConsoleMenu(ConsoleInput input, PrintStream out, UserService userService, CourseService courseService,
                       QuizService quizService, GradeService gradeService, NotificationService notificationService) {
        this(input, out, userService, courseService, quizService, gradeService, notificationService,
                new RecommendationService(new InterestBasedRecommendationStrategy()));
    }

    public ConsoleMenu(ConsoleInput input, PrintStream out, UserService userService, CourseService courseService,
                       QuizService quizService, GradeService gradeService, NotificationService notificationService,
                       RecommendationService recommendationService) {
        this.input = input;
        this.out = out;
        this.userService = userService;
        this.courseService = courseService;
        this.quizService = quizService;
        this.gradeService = gradeService;
        this.notificationService = notificationService;
        this.facade = new EducationPlatformFacade(userService, courseService, quizService, gradeService,
                notificationService, recommendationService);
        this.courseCreationWizard = new CourseCreationWizard(input, out, userService, courseService, facade);
        this.demoDataLoader = new DemoDataLoader(userService, courseService, notificationService);
        registerMenuItems();
    }

    private void registerMenuItems() {
        addMenuItem("Register User", this::registerUser);
        addMenuItem("View All Users", this::viewAllUsers);
        addMenuItem("Create Course", courseCreationWizard::run);
        addMenuItem("View All Courses", this::viewAllCourses);
        addMenuItem("Enroll Student in Course", this::enrollStudent);
        addMenuItem("View Course Lessons", this::viewCourseDetails);
        addMenuItem("Complete Quiz", this::completeQuiz);
        addMenuItem("View Student Grades", this::viewStudentGrades);
        addMenuItem("View Notifications", this::viewNotifications);
        addMenuItem("Manage Notification Subscription", this::manageSubscription);
        addMenuItem("Platform Overview (Admin)", this::showPlatformOverview);
        addMenuItem("Load Demo Data", this::loadDemoData);
        addMenuItem("Set Student Interests", this::setStudentInterests);
        addMenuItem("Recommend Courses", this::recommendCourses);
        addMenuItem("Switch Recommendation Strategy", this::switchRecommendationStrategy);
        addMenuItem("Add Additional Materials", this::addMaterials);
        addMenuItem("View Course Materials", this::viewMaterials);
        addMenuItem("Enable Course Certificate", this::enableCertificate);
        addMenuItem("View Progress / Request Certificate", this::viewProgressAndCertificate);
        addMenuItem("Mark Lesson Complete", this::markLessonComplete);
    }

    private void addMenuItem(String label, Runnable action) {
        menuItems.put(menuItems.size() + 1, new MenuItem(label, action));
    }

    public void run() {
        try {
            int choice;
            do {
                printMenu();
                choice = input.readInt("Select an option: ", EXIT_OPTION, menuItems.size());
                if (choice != EXIT_OPTION) {
                    execute(menuItems.get(choice));
                }
            } while (choice != EXIT_OPTION);
        } catch (NoSuchElementException endOfInput) {
            out.println();
            out.println("Input closed.");
        }
        out.println("Goodbye!");
    }

    private void printMenu() {
        out.println();
        out.println("EDUPLATFORM");
        out.println(SEPARATOR);
        menuItems.forEach((number, item) -> out.printf("%d. %s%n", number, item.label()));
        out.println(EXIT_OPTION + ". Exit");
        out.println(SEPARATOR);
    }

    private void execute(MenuItem item) {
        out.println();
        out.println("--- " + item.label() + " ---");
        try {
            item.action().run();
        } catch (IllegalArgumentException | IllegalStateException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void registerUser() {
        UserRole role = input.choose("Select a role:", List.of(UserRole.values()), UserRole::name, "").orElseThrow();
        String name = input.readNonBlank("Name: ");
        String email = input.readNonBlank("Email: ");
        User user = facade.registerUser(role, name, email);
        out.println("Registered: " + user);
        if (user instanceof Student student) {
            notificationService.subscribe(student);
            out.println("Student subscribed to grade notifications.");
        }
    }

    private void viewAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            out.println("No users registered yet.");
            return;
        }
        users.forEach(user -> out.printf("%s%n    %s%n", user, user.getDashboardSummary()));
    }

    private void viewAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            out.println("No courses created yet.");
            return;
        }
        courses.forEach(course -> out.printf("%s%n    %d lesson(s), %d quiz(zes)%n",
                course, course.getLessons().size(), course.getQuizzes().size()));
    }

    private void enrollStudent() {
        Optional<Student> student = chooseStudent();
        if (student.isEmpty()) {
            return;
        }
        Optional<Course> course = chooseCourse(courseService.getAllCourses(), "No courses created yet.");
        if (course.isEmpty()) {
            return;
        }
        facade.enroll(student.get().getId(), course.get().getId());
        out.println(student.get().getName() + " enrolled in '" + course.get().getTitle() + "'.");
    }

    private void viewCourseDetails() {
        Optional<Course> selected = chooseCourse(courseService.getAllCourses(), "No courses created yet.");
        if (selected.isEmpty()) {
            return;
        }
        Course course = selected.get();
        out.println(course);
        if (!course.getDescription().isEmpty()) {
            out.println("Description: " + course.getDescription());
        }
        out.println("Lessons:");
        List<Lesson> lessons = course.getLessons();
        for (int i = 0; i < lessons.size(); i++) {
            out.printf("  %d. %s%n", i + 1, lessons.get(i));
            if (!lessons.get(i).getContent().isEmpty()) {
                out.println("     " + lessons.get(i).getContent());
            }
        }
        out.println("Quizzes:");
        if (course.getQuizzes().isEmpty()) {
            out.println("  (none)");
        }
        course.getQuizzes().forEach(quiz -> out.println("  - " + quiz));
    }

    private void completeQuiz() {
        Optional<Student> selectedStudent = chooseStudent();
        if (selectedStudent.isEmpty()) {
            return;
        }
        Student student = selectedStudent.get();
        Optional<Course> course = chooseCourse(student.getEnrolledCourses(),
                student.getName() + " is not enrolled in any course yet.");
        if (course.isEmpty()) {
            return;
        }
        Optional<Quiz> quiz = input.choose("Select a quiz:", course.get().getQuizzes(), Quiz::toString,
                "This course has no quizzes.");
        if (quiz.isEmpty()) {
            return;
        }

        List<Integer> answers = askQuestions(quiz.get());
        int notificationsBefore = student.getNotifications().size();
        Grade grade = facade.submitQuiz(student.getId(), course.get().getId(), quiz.get().getTitle(), answers);
        int newNotifications = student.getNotifications().size() - notificationsBefore;

        out.println("Quiz completed. Score: " + grade.getScoreSummary());
        if (newNotifications > 0) {
            out.printf("Observer delivered %d new notification(s) to %s (see 'View Notifications').%n",
                    newNotifications, student.getName());
        } else {
            out.println(student.getName() + " is not subscribed to grade notifications, so none was delivered.");
        }
    }

    private List<Integer> askQuestions(Quiz quiz) {
        List<Integer> answers = new ArrayList<>();
        List<Question> questions = quiz.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            out.printf("Q%d. %s%n", i + 1, question.getText());
            List<String> options = question.getOptions();
            for (int j = 0; j < options.size(); j++) {
                out.printf("   %d) %s%n", j + 1, options.get(j));
            }
            answers.add(input.readInt("Your answer: ", 1, options.size()) - 1);
        }
        return answers;
    }

    private void viewStudentGrades() {
        Optional<Student> student = chooseStudent();
        if (student.isEmpty()) {
            return;
        }
        List<Grade> grades = facade.grades(student.get().getId());
        if (grades.isEmpty()) {
            out.println(student.get().getName() + " has no grades yet.");
            return;
        }
        out.println("Grades of " + student.get().getName() + ":");
        grades.forEach(grade -> out.println("  " + grade));
    }

    private void viewNotifications() {
        Optional<Student> student = chooseStudent();
        if (student.isEmpty()) {
            return;
        }
        List<Notification> notifications = facade.notifications(student.get().getId());
        if (notifications.isEmpty()) {
            out.println(student.get().getName() + " has no notifications.");
            return;
        }
        out.println("Notifications for " + student.get().getName() + ":");
        notifications.forEach(notification -> out.printf("  %s%n    Course: %s | Quiz: %s | Score: %s%n",
                notification, notification.getCourseTitle(), notification.getQuizTitle(),
                notification.getScoreSummary()));
    }

    private void manageSubscription() {
        Optional<Student> selected = chooseStudent();
        if (selected.isEmpty()) {
            return;
        }
        Student student = selected.get();
        if (facade.isSubscribed(student.getId())) {
            out.println(student.getName() + " is currently SUBSCRIBED to grade notifications.");
            if (input.readYesNo("Unsubscribe?")) {
                facade.unsubscribe(student.getId());
                out.println(student.getName() + " unsubscribed.");
            }
        } else {
            out.println(student.getName() + " is currently NOT subscribed to grade notifications.");
            if (input.readYesNo("Subscribe?")) {
                facade.subscribe(student.getId());
                out.println(student.getName() + " subscribed.");
            }
        }
    }

    private void showPlatformOverview() {
        Optional<Admin> admin = input.choose("Select an administrator:", userService.usersOfType(Admin.class),
                Admin::toString, "Only administrators can view the platform overview. Register an admin first.");
        if (admin.isEmpty()) {
            return;
        }
        out.println("Platform overview requested by " + admin.get().getName() + ":");
        admin.get().countUsersByRole(userService.getAllUsers())
                .forEach((role, count) -> out.printf("  %-8s %d%n", role, count));
        out.printf("  Courses  %d%n", courseService.getAllCourses().size());
        out.printf("  Grades   %d%n", gradeService.getAllGrades().size());
    }

    private void loadDemoData() {
        demoDataLoader.load();
        out.println("Demo data loaded: 1 admin, 1 teacher, 2 students, 1 course with 3 lessons and 2 quizzes.");
        out.println("Both students are enrolled in 'Java Programming' and subscribed to notifications.");
    }

    private void setStudentInterests() {
        Optional<Student> student = chooseStudent(); if (student.isEmpty()) return;
        String raw = input.readNonBlank("Interests, separated by commas: ");
        facade.setInterests(student.get().getId(), java.util.Arrays.stream(raw.split(",")).map(String::trim).filter(s -> !s.isBlank()).collect(java.util.stream.Collectors.toSet()));
        out.println("Interests saved.");
    }
    private void recommendCourses() {
        Optional<Student> student = chooseStudent(); if (student.isEmpty()) return;
        List<com.eduplatform.recommendation.CourseRecommendation> items = facade.recommend(student.get().getId(), 10);
        if (items.isEmpty()) { out.println("No matching available courses."); return; }
        items.forEach(item -> out.printf("  %s (%.1f): %s%n", item.course().getTitle(), item.score(), String.join(", ", item.reasons())));
    }
    private void switchRecommendationStrategy() {
        List<RecommendationStrategy> strategies = List.of(new InterestBasedRecommendationStrategy(), new PerformanceBasedRecommendationStrategy());
        Optional<RecommendationStrategy> selected = input.choose("Select recommendation algorithm:", strategies, RecommendationStrategy::name, "No strategies available.");
        selected.ifPresent(facade::switchRecommendationStrategy);
    }
    private void addMaterials() {
        Optional<Course> course = chooseCourse(courseService.getAllCourses(), "No courses created yet."); if (course.isEmpty()) return;
        String title = input.readNonBlank("Material title: "); String location = input.readNonBlank("Material URL or path: ");
        facade.addMaterials(course.get().getId(), List.of(new LearningMaterial(title, location)));
        out.println("Additional material added.");
    }
    private void viewMaterials() {
        Optional<Course> course = chooseCourse(courseService.getAllCourses(), "No courses created yet."); if (course.isEmpty()) return;
        List<LearningMaterial> materials = facade.materials(course.get().getId());
        if (materials.isEmpty()) out.println("No additional materials."); else materials.forEach(m -> out.println("  " + m.title() + " — " + m.location()));
    }
    private void enableCertificate() {
        Optional<Course> course = chooseCourse(courseService.getAllCourses(), "No courses created yet."); if (course.isEmpty()) return;
        facade.enableCertificate(course.get().getId()); out.println("Certificate support enabled. Certificates are issued only after course completion.");
    }
    private void viewProgressAndCertificate() {
        Optional<Student> student = chooseStudent(); if (student.isEmpty()) return;
        Optional<Course> course = chooseCourse(student.get().getEnrolledCourses(), "Student is not enrolled in any course."); if (course.isEmpty()) return;
        CourseProgress progress = facade.progress(student.get().getId(), course.get().getId());
        out.println("Progress: " + progress.completionPercent() + "%");
        if (input.readYesNo("Request certificate?")) {
            var cert = facade.requestCertificate(student.get().getId(), course.get().getId());
            out.println(cert.map(c -> "Certificate: " + c.certificateId()).orElse("Certificate unavailable: complete all lessons and quizzes and ensure support is enabled."));
        }
    }
    private void markLessonComplete() {
        Optional<Student> student = chooseStudent(); if (student.isEmpty()) return;
        Optional<Course> course = chooseCourse(student.get().getEnrolledCourses(), "Student is not enrolled in any course."); if (course.isEmpty()) return;
        Optional<Lesson> lesson = input.choose("Select completed lesson:", course.get().getLessons(), Lesson::getTitle, "Course has no lessons.");
        lesson.ifPresent(value -> { facade.markLessonCompleted(student.get().getId(), course.get().getId(), value.getTitle()); out.println("Lesson completion recorded."); });
    }

    private Optional<Student> chooseStudent() {
        return input.choose("Select a student:", userService.getStudents(), Student::toString,
                "No students registered yet.");
    }

    private Optional<Course> chooseCourse(List<Course> courses, String emptyMessage) {
        return input.choose("Select a course:", courses, Course::toString, emptyMessage);
    }

    private record MenuItem(String label, Runnable action) {
    }
}
