package com.eduplatform.facade;

import com.eduplatform.decorator.AdditionalMaterialsDecorator;
import com.eduplatform.decorator.BaseCourse;
import com.eduplatform.decorator.CertificateDecorator;
import com.eduplatform.decorator.CourseComponent;
import com.eduplatform.factory.UserFactory;
import com.eduplatform.model.*;
import com.eduplatform.recommendation.*;
import com.eduplatform.service.*;
import com.eduplatform.observer.GradeService;
import java.util.*;
import java.util.function.Consumer;

/** One application API. It validates cross-service operations and leaves domain rules in services. */
public final class EducationPlatformFacade {
    private final UserService users;
    private final CourseService courses;
    private final QuizService quizzes;
    private final GradeService grades;
    private final NotificationService notifications;
    private final RecommendationService recommendations;
    private final Map<String, CourseComponent> decoratedCourses = new HashMap<>();

    public EducationPlatformFacade(UserService users, CourseService courses, QuizService quizzes,
            GradeService grades, NotificationService notifications, RecommendationService recommendations) {
        this.users = Objects.requireNonNull(users); this.courses = Objects.requireNonNull(courses);
        this.quizzes = Objects.requireNonNull(quizzes); this.grades = Objects.requireNonNull(grades);
        this.notifications = Objects.requireNonNull(notifications); this.recommendations = Objects.requireNonNull(recommendations);
    }
    public User registerUser(UserRole role, String name, String email) { return users.registerUser(role, name, email); }
    public Course createCourse(String teacherId, Consumer<Course.Builder> configure) {
        Teacher teacher = require(users.findById(teacherId).filter(Teacher.class::isInstance).map(Teacher.class::cast), "Teacher", teacherId);
        Course.Builder builder = courses.newCourseBuilder(teacher);
        Objects.requireNonNull(configure).accept(builder);
        Course course = courses.addCourse(builder.build());
        decoratedCourses.put(course.getId(), new BaseCourse(course));
        return course;
    }
    public void setInterests(String studentId, Set<String> interests) { requireStudent(studentId).setInterests(interests); }
    public List<CourseRecommendation> recommend(String studentId, int limit) {
        Student student = requireStudent(studentId);
        List<Grade> studentGrades = grades.getGradesForStudent(student);
        double average = studentGrades.isEmpty() ? 0.0 : studentGrades.stream().mapToInt(Grade::getScorePercentage).average().orElse(0.0);
        return recommendations.recommend(new StudentRecommendationProfile(student, average), courses.getAllCourses(), limit);
    }
    public void switchRecommendationStrategy(RecommendationStrategy strategy) { recommendations.setStrategy(strategy); }
    public void enroll(String studentId, String courseId) { courses.enroll(requireStudent(studentId), requireCourse(courseId)); }
    public CourseComponent addMaterials(String courseId, List<LearningMaterial> materials) {
        CourseComponent next = new AdditionalMaterialsDecorator(component(courseId), materials);
        decoratedCourses.put(courseId, next); return next;
    }
    public CourseComponent enableCertificate(String courseId) {
        CourseComponent existing = component(courseId);
        if (!existing.certificateEnabled()) existing = new CertificateDecorator(existing);
        decoratedCourses.put(courseId, existing); return existing;
    }
    public List<LearningMaterial> materials(String courseId) { return component(courseId).materials(); }
    public Grade submitQuiz(String studentId, String courseId, String quizTitle, List<Integer> answers) {
        Student student = requireStudent(studentId); Course course = requireCourse(courseId);
        Quiz quiz = course.getQuizzes().stream().filter(item -> item.getTitle().equals(quizTitle)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Quiz is not part of this course: " + quizTitle));
        return quizzes.submitQuiz(student, course, quiz, answers);
    }
    public List<Grade> grades(String studentId) { return grades.getGradesForStudent(requireStudent(studentId)); }
    public List<Notification> notifications(String studentId) { return notifications.getNotifications(requireStudent(studentId)); }
    public CourseProgress progress(String studentId, String courseId) {
        Student student = requireStudent(studentId); Course course = requireCourse(courseId);
        if (!student.isEnrolledIn(course)) throw new IllegalStateException("Student is not enrolled in course");
        int total = course.getLessons().size() + course.getQuizzes().size();
        int percent = total == 0 ? 0 : (int) Math.round(100.0 * student.getCompletedActivities(course) / total);
        return new CourseProgress(courseId, Math.min(percent, 100));
    }
    public Optional<Certificate> requestCertificate(String studentId, String courseId) {
        Student student = requireStudent(studentId);
        CourseProgress progress = progress(studentId, courseId);
        return component(courseId).issueCertificate(student, progress.completionPercent());
    }
    public void markLessonCompleted(String studentId, String courseId, String lessonTitle) {
        Student student = requireStudent(studentId); Course course = requireCourse(courseId);
        Lesson lesson = course.getLessons().stream().filter(item -> item.getTitle().equals(lessonTitle)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Lesson is not part of this course"));
        student.markLessonCompleted(course, lesson);
    }
    public void subscribe(Student student) { notifications.subscribe(student); }
    public void subscribe(String studentId) { notifications.subscribe(requireStudent(studentId)); }
    public boolean unsubscribe(String studentId) { return notifications.unsubscribe(requireStudent(studentId)); }
    public boolean isSubscribed(String studentId) { return notifications.isSubscribed(requireStudent(studentId)); }
    public UserService userService() { return users; }
    public CourseService courseService() { return courses; }
    public GradeService gradeService() { return grades; }
    private CourseComponent component(String id) { Course course = requireCourse(id); return decoratedCourses.computeIfAbsent(id, ignored -> new BaseCourse(course)); }
    private Student requireStudent(String id) { return require(users.findById(id).filter(Student.class::isInstance).map(Student.class::cast), "Student", id); }
    private Course requireCourse(String id) { return require(courses.findById(id), "Course", id); }
    private static <T> T require(Optional<T> value, String kind, String id) { return value.orElseThrow(() -> new NoSuchElementException(kind + " not found: " + id)); }
}
