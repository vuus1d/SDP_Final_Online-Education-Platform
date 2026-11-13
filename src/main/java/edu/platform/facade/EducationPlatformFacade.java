package edu.platform.facade;

import edu.platform.course.AdditionalMaterialsDecorator;
import edu.platform.course.BaseCourse;
import edu.platform.course.CertificateDecorator;
import edu.platform.course.CourseComponent;
import edu.platform.domain.*;
import edu.platform.facade.port.*;
import edu.platform.recommendation.CourseRecommendation;
import edu.platform.recommendation.RecommendationService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Application entry point coordinating platform ports. Business rules remain in their owning subsystems.
 * Grade notifications are emitted by the injected grade subsystem's Observer integration.
 */
public final class EducationPlatformFacade {
    private final StudentFactoryPort studentFactory;
    private final CourseBuilderPort courseBuilder;
    private final CourseCatalogPort courses;
    private final StudentDirectoryPort students;
    private final EnrollmentPort enrollment;
    private final CourseExtensionStorePort extensions;
    private final RecommendationService recommendations;
    private final QuizSubsystemPort quizzes;
    private final GradeSubsystemPort grades;
    private final ProgressQueryPort progress;

    public EducationPlatformFacade(StudentFactoryPort studentFactory, CourseBuilderPort courseBuilder,
            CourseCatalogPort courses, StudentDirectoryPort students, EnrollmentPort enrollment,
            CourseExtensionStorePort extensions, RecommendationService recommendations,
            QuizSubsystemPort quizzes, GradeSubsystemPort grades, ProgressQueryPort progress) {
        this.studentFactory = Objects.requireNonNull(studentFactory, "student factory must not be null");
        this.courseBuilder = Objects.requireNonNull(courseBuilder, "course builder must not be null");
        this.courses = Objects.requireNonNull(courses, "course catalog must not be null");
        this.students = Objects.requireNonNull(students, "student directory must not be null");
        this.enrollment = Objects.requireNonNull(enrollment, "enrollment service must not be null");
        this.extensions = Objects.requireNonNull(extensions, "extension store must not be null");
        this.recommendations = Objects.requireNonNull(recommendations, "recommendation service must not be null");
        this.quizzes = Objects.requireNonNull(quizzes, "quiz subsystem must not be null");
        this.grades = Objects.requireNonNull(grades, "grade subsystem must not be null");
        this.progress = Objects.requireNonNull(progress, "progress query must not be null");
    }

    public StudentProfile registerStudent(StudentRegistrationRequest request) {
        StudentProfile student = Objects.requireNonNull(studentFactory.createStudent(Objects.requireNonNull(request, "request must not be null")), "factory returned null student");
        if (!student.id().equals(request.id())) throw new IllegalStateException("factory returned a different student id");
        return student;
    }

    public Course createCourse(CourseDraft draft) {
        Course course = Objects.requireNonNull(courseBuilder.build(Objects.requireNonNull(draft, "draft must not be null")), "builder returned null course");
        if (!course.id().equals(draft.id())) throw new IllegalStateException("builder returned a different course id");
        courses.saveCourse(course);
        extensions.save(new BaseCourse(course));
        return course;
    }

    public List<CourseRecommendation> getRecommendedCourses(StudentId studentId, int limit) {
        StudentProfile student = requireStudent(studentId);
        return recommendations.recommend(student, courses.availableCourses(), limit);
    }

    public void enrollStudent(StudentId studentId, CourseId courseId) {
        requireStudent(studentId);
        Course course = requireCourse(courseId);
        if (!course.available()) throw new IllegalStateException("course is not available: " + courseId.value());
        enrollment.enroll(studentId, courseId);
    }

    public CourseComponent addCourseMaterials(CourseId courseId, List<LearningMaterial> materials) {
        CourseComponent decorated = new AdditionalMaterialsDecorator(requireCourseComponent(courseId), materials);
        extensions.save(decorated);
        return decorated;
    }

    public CourseComponent enableCourseCertificate(CourseId courseId) {
        CourseComponent decorated = new CertificateDecorator(requireCourseComponent(courseId));
        extensions.save(decorated);
        return decorated;
    }

    public QuizResult submitQuiz(StudentId studentId, CourseId courseId, String quizId, Map<String, String> answers) {
        requireStudent(studentId);
        requireCourse(courseId);
        Objects.requireNonNull(quizId, "quiz id must not be null");
        Objects.requireNonNull(answers, "answers must not be null");
        return Objects.requireNonNull(quizzes.submit(studentId, courseId, quizId, Map.copyOf(answers)), "quiz subsystem returned null");
    }

    public Grade assignGrade(Grade grade) {
        Objects.requireNonNull(grade, "grade must not be null");
        requireStudent(grade.studentId());
        requireCourse(grade.courseId());
        // Observer notification belongs to the grade subsystem; facade performs exactly one delegation.
        return Objects.requireNonNull(grades.assign(grade), "grade subsystem returned null");
    }

    public CourseProgress getStudentProgress(StudentId studentId, CourseId courseId) {
        requireStudent(studentId);
        requireCourse(courseId);
        CourseProgress result = Objects.requireNonNull(progress.progress(studentId, courseId), "progress subsystem returned null");
        if (!result.courseId().equals(courseId)) throw new IllegalStateException("progress subsystem returned another course");
        return result;
    }

    public Optional<Certificate> requestCertificate(StudentId studentId, CourseId courseId) {
        requireStudent(studentId);
        CourseProgress courseProgress = getStudentProgress(studentId, courseId);
        return requireCourseComponent(courseId).issueCertificate(studentId, courseProgress);
    }

    private StudentProfile requireStudent(StudentId id) {
        Objects.requireNonNull(id, "student id must not be null");
        return students.findStudent(id).orElseThrow(() -> new ResourceNotFoundException("Student", id.value()));
    }
    private Course requireCourse(CourseId id) {
        Objects.requireNonNull(id, "course id must not be null");
        return courses.findCourse(id).orElseThrow(() -> new ResourceNotFoundException("Course", id.value()));
    }
    private CourseComponent requireCourseComponent(CourseId id) {
        Course course = requireCourse(id);
        return extensions.find(id).orElseGet(() -> new BaseCourse(course));
    }
}
