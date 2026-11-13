package edu.platform.course;

import edu.platform.domain.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class CourseDecoratorTest {
    private final Course course = new Course(new CourseId("java"), "Java Course", "Description", Set.of("Java"), 25, true);
    private final LearningMaterial material = new LearningMaterial("Extra notes", "notes.pdf");
    private final StudentId student = new StudentId("s1");
    private final Instant fixedInstant = Instant.parse("2026-01-02T03:04:05Z");

    @Test void baseCourseHasNoExtraFeatures() {
        CourseComponent base = new BaseCourse(course);
        assertSame(course, base.course());
        assertTrue(base.materials().isEmpty());
        assertFalse(base.certificateEnabled());
        assertTrue(base.issueCertificate(student, new CourseProgress(course.id(), 100)).isEmpty());
    }

    @Test void materialsDecoratorAddsMaterialsWithoutChangingCourseOrOtherComponent() {
        CourseComponent base = new BaseCourse(course);
        CourseComponent decorated = new AdditionalMaterialsDecorator(base, List.of(material));
        assertEquals(List.of(material), decorated.materials());
        assertSame(course, decorated.course());
        assertTrue(base.materials().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> decorated.materials().add(material));
    }

    @Test void certificateRequiresExplicitRequestAndCompleteProgressForWrappedCourse() {
        CourseComponent decorated = new CertificateDecorator(new BaseCourse(course), Clock.fixed(fixedInstant, ZoneOffset.UTC));
        assertTrue(decorated.certificateEnabled());
        assertTrue(decorated.issueCertificate(student, new CourseProgress(course.id(), 99)).isEmpty());
        assertTrue(decorated.issueCertificate(student, new CourseProgress(new CourseId("other"), 100)).isEmpty());
        var issued = decorated.issueCertificate(student, new CourseProgress(course.id(), 100)).orElseThrow();
        assertEquals(course.id(), issued.courseId());
        assertEquals(student, issued.studentId());
        assertEquals(fixedInstant, issued.issuedAt());
        assertNotNull(issued.certificateId());
    }

    @Test void decoratorsComposeInEitherOrderAndRetainBaseIdentity() {
        CourseComponent materialsThenCertificate = new CertificateDecorator(
                new AdditionalMaterialsDecorator(new BaseCourse(course), List.of(material)), Clock.fixed(fixedInstant, ZoneOffset.UTC));
        CourseComponent certificateThenMaterials = new AdditionalMaterialsDecorator(
                new CertificateDecorator(new BaseCourse(course), Clock.fixed(fixedInstant, ZoneOffset.UTC)), List.of(material));
        for (CourseComponent component : List.of(materialsThenCertificate, certificateThenMaterials)) {
            assertSame(course, component.course());
            assertEquals(List.of(material), component.materials());
            assertTrue(component.certificateEnabled());
            assertTrue(component.issueCertificate(student, new CourseProgress(course.id(), 100)).isPresent());
        }
    }

    @Test void invalidMaterialsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AdditionalMaterialsDecorator(new BaseCourse(course), List.of()));
        assertThrows(NullPointerException.class, () -> new AdditionalMaterialsDecorator(new BaseCourse(course), null));
    }
}
