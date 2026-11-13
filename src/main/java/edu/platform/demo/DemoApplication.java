package edu.platform.demo;

import edu.platform.course.*;
import edu.platform.domain.*;
import edu.platform.facade.EducationPlatformFacade;
import edu.platform.facade.port.*;
import edu.platform.recommendation.*;
import java.util.*;

/** Runnable demo for this branch's patterns. Other-pattern ports are transparent local demo adapters. */
public final class DemoApplication {
    private DemoApplication() {}
    public static void main(String[] args) {
        DemoPorts ports = new DemoPorts();
        RecommendationService recommendationService = new RecommendationService(new InterestBasedRecommendationStrategy());
        EducationPlatformFacade platform = new EducationPlatformFacade(
                request -> {
                    StudentProfile student = new StudentProfile(request.id(), request.interests(), 82, Set.of());
                    ports.students.put(student.id(), student);
                    return student;
                },
                draft -> new Course(draft.id(), draft.title(), draft.description(), draft.topics(), draft.minimumPerformance(), draft.available()),
                ports, ports, (studentId, courseId) -> System.out.println("Enrolled " + studentId.value() + " in " + courseId.value()),
                ports, recommendationService,
                (studentId, courseId, quizId, answers) -> new QuizResult(quizId, studentId, courseId, 92),
                grade -> { System.out.println("Grade subsystem accepted grade; Observer delivery is pending team integration."); return grade; },
                (studentId, courseId) -> new CourseProgress(courseId, 100));

        StudentId studentId = new StudentId("student-1");
        CourseId javaId = new CourseId("java-backend");
        platform.registerStudent(new StudentRegistrationRequest(studentId, "Alex", Set.of("Java", "Backend")));
        platform.createCourse(new CourseDraft(javaId, "Java Backend", "Build backend services.", Set.of("Java", "Backend"), 65, true));
        platform.createCourse(new CourseDraft(new CourseId("ui-design"), "UI Design", "Design usable interfaces.", Set.of("Design", "Frontend"), 30, true));

        System.out.println("Interest recommendations: " + titles(platform.getRecommendedCourses(studentId, 5)));
        recommendationService.setStrategy(new PerformanceBasedRecommendationStrategy());
        System.out.println("Performance recommendations: " + titles(platform.getRecommendedCourses(studentId, 5)));

        platform.enrollStudent(studentId, javaId);
        platform.addCourseMaterials(javaId, List.of(new LearningMaterial("Spring guide", "https://example.invalid/spring")));
        CourseComponent decorated = platform.enableCourseCertificate(javaId);
        System.out.println("Decorated course: " + decorated.course().title() + "; materials=" + decorated.materials().size() + "; certificate feature=" + decorated.certificateEnabled());
        platform.submitQuiz(studentId, javaId, "java-basics", Map.of("question-1", "answer"));
        platform.assignGrade(new Grade(studentId, javaId, 92, "Strong work"));
        System.out.println("Certificate issued after completion: " + platform.requestCertificate(studentId, javaId).isPresent());
    }
    private static List<String> titles(List<CourseRecommendation> recommendations) {
        return recommendations.stream().map(result -> result.course().title()).toList();
    }

    /** In-memory demo adapter only; it does not implement or claim the Builder, Factory, or Observer patterns. */
    private static final class DemoPorts implements CourseCatalogPort, StudentDirectoryPort, CourseExtensionStorePort {
        private final Map<CourseId, Course> catalog = new LinkedHashMap<>();
        private final Map<StudentId, StudentProfile> students = new LinkedHashMap<>();
        private final Map<CourseId, CourseComponent> decorated = new LinkedHashMap<>();
        @Override public Optional<Course> findCourse(CourseId id) { return Optional.ofNullable(catalog.get(id)); }
        @Override public List<Course> availableCourses() { return catalog.values().stream().filter(Course::available).toList(); }
        @Override public void saveCourse(Course course) { catalog.put(course.id(), course); }
        @Override public Optional<StudentProfile> findStudent(StudentId id) { return Optional.ofNullable(students.get(id)); }
        @Override public Optional<CourseComponent> find(CourseId id) { return Optional.ofNullable(decorated.get(id)); }
        @Override public void save(CourseComponent course) { decorated.put(course.course().id(), course); }
    }
}
