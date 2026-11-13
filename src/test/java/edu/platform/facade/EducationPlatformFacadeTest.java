package edu.platform.facade;

import edu.platform.course.*;
import edu.platform.domain.*;
import edu.platform.facade.port.*;
import edu.platform.recommendation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class EducationPlatformFacadeTest {
    private Fixture fixture;
    private EducationPlatformFacade facade;
    private final StudentId studentId = new StudentId("student");
    private final CourseId courseId = new CourseId("course");

    @BeforeEach void setUp() { fixture = new Fixture(); facade = fixture.facade(); }

    @Test void facadeCoordinatesRegistrationCreationRecommendationAndEnrollment() {
        StudentProfile createdStudent = facade.registerStudent(new StudentRegistrationRequest(studentId, "Sam", Set.of("Java")));
        assertEquals(studentId, createdStudent.id());
        Course course = facade.createCourse(new CourseDraft(courseId, "Java", "Java course", Set.of("Java"), 0, true));
        assertSame(course, fixture.catalog.get(courseId));
        assertTrue(facade.getRecommendedCourses(studentId, 3).stream().anyMatch(r -> r.course().id().equals(courseId)));
        facade.enrollStudent(studentId, courseId);
        assertEquals(List.of(courseId), fixture.enrollments);
    }

    @Test void facadeDecoratesExistingCourseAndRequestsCertificateOnlyAfterProgressCheck() {
        fixture.seed();
        CourseComponent materials = facade.addCourseMaterials(courseId, List.of(new LearningMaterial("Notes", "notes.pdf")));
        assertEquals(1, materials.materials().size());
        CourseComponent withCertificate = facade.enableCourseCertificate(courseId);
        assertTrue(withCertificate.certificateEnabled());
        assertEquals(1, withCertificate.materials().size(), "enabling certificates must retain existing course materials");
        fixture.completion = 50;
        assertTrue(facade.requestCertificate(studentId, courseId).isEmpty());
        fixture.completion = 100;
        assertTrue(facade.requestCertificate(studentId, courseId).isPresent());
    }

    @Test void facadeDelegatesQuizAndGradeOnceAndPropagatesSubsystemErrors() {
        fixture.seed();
        assertEquals(1, facade.submitQuiz(studentId, courseId, "q1", Map.of("a", "b")).scorePercent());
        Grade grade = new Grade(studentId, courseId, 90, "Good");
        assertSame(grade, facade.assignGrade(grade));
        assertEquals(1, fixture.gradeCalls);
        assertEquals(0, fixture.observerDeliveryCount, "facade must not duplicate Observer effects");
        fixture.failGrade = true;
        assertThrows(IllegalStateException.class, () -> facade.assignGrade(grade));
        assertEquals(2, fixture.gradeCalls);
    }

    @Test void facadeRejectsMissingResourcesBeforeDelegating() {
        fixture.seed();
        assertThrows(ResourceNotFoundException.class, () -> facade.enrollStudent(new StudentId("missing"), courseId));
        assertTrue(fixture.enrollments.isEmpty());
        assertThrows(ResourceNotFoundException.class, () -> facade.enrollStudent(studentId, new CourseId("missing")));
        assertThrows(ResourceNotFoundException.class, () -> facade.getRecommendedCourses(new StudentId("missing"), 2));
        assertThrows(ResourceNotFoundException.class, () -> facade.addCourseMaterials(new CourseId("missing"), List.of(new LearningMaterial("M", "m"))));
    }

    @Test void facadeChecksUnavailableCoursesAndValidatesArguments() {
        fixture.seed();
        fixture.catalog.put(courseId, new Course(courseId, "Course", "Description", Set.of("Java"), 0, false));
        assertThrows(IllegalStateException.class, () -> facade.enrollStudent(studentId, courseId));
        assertThrows(NullPointerException.class, () -> facade.registerStudent(null));
        assertThrows(NullPointerException.class, () -> facade.assignGrade(null));
        assertThrows(IllegalArgumentException.class, () -> facade.getRecommendedCourses(studentId, -1));
    }

    private static final class Fixture implements CourseCatalogPort, StudentDirectoryPort, CourseExtensionStorePort {
        final Map<CourseId, Course> catalog = new LinkedHashMap<>();
        final Map<StudentId, StudentProfile> students = new LinkedHashMap<>();
        final Map<CourseId, CourseComponent> extensionMap = new HashMap<>();
        final List<CourseId> enrollments = new ArrayList<>();
        int gradeCalls;
        int observerDeliveryCount;
        int completion = 100;
        boolean failGrade;
        EducationPlatformFacade facade() {
            return new EducationPlatformFacade(
                    request -> { var student = new StudentProfile(request.id(), request.interests(), 75, Set.of()); students.put(student.id(), student); return student; },
                    draft -> new Course(draft.id(), draft.title(), draft.description(), draft.topics(), draft.minimumPerformance(), draft.available()),
                    this, this, (student, course) -> enrollments.add(course), this,
                    new RecommendationService(new InterestBasedRecommendationStrategy()),
                    (student, course, quiz, answers) -> new QuizResult(quiz, student, course, 1),
                    grade -> { gradeCalls++; if (failGrade) throw new IllegalStateException("grading failed"); return grade; },
                    (student, course) -> new CourseProgress(course, completion));
        }
        void seed() {
            StudentId fixtureStudentId = new StudentId("student");
            CourseId fixtureCourseId = new CourseId("course");
            students.put(fixtureStudentId, new StudentProfile(fixtureStudentId, Set.of("Java"), 75, Set.of()));
            catalog.put(fixtureCourseId, new Course(fixtureCourseId, "Course", "Description", Set.of("Java"), 0, true));
        }
        @Override public Optional<Course> findCourse(CourseId id) { return Optional.ofNullable(catalog.get(id)); }
        @Override public List<Course> availableCourses() { return catalog.values().stream().filter(Course::available).toList(); }
        @Override public void saveCourse(Course course) { catalog.put(course.id(), course); }
        @Override public Optional<StudentProfile> findStudent(StudentId id) { return Optional.ofNullable(students.get(id)); }
        @Override public Optional<CourseComponent> find(CourseId id) { return Optional.ofNullable(extensionMap.get(id)); }
        @Override public void save(CourseComponent component) { extensionMap.put(component.course().id(), component); }
    }
}
