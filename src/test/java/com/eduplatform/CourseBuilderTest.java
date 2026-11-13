package com.eduplatform;

import com.eduplatform.factory.UserFactory;
import com.eduplatform.model.Course;
import com.eduplatform.model.DifficultyLevel;
import com.eduplatform.model.Lesson;
import com.eduplatform.model.Quiz;
import com.eduplatform.model.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseBuilderTest {

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = UserFactory.createTeacher("John", "john@example.com");
    }

    private Course.Builder validBuilder() {
        return Course.builder()
                .setId("CRS-1")
                .setTitle("Java Programming")
                .setDescription("Learn Java from basics to OOP")
                .setTeacher(teacher)
                .addLesson(TestData.lesson("Intro"))
                .setDuration(30)
                .setDifficultyLevel("BEGINNER");
    }

    @Test
    void buildsValidCourse() {
        Course course = validBuilder().build();

        assertEquals("CRS-1", course.getId());
        assertEquals("Java Programming", course.getTitle());
        assertEquals("Learn Java from basics to OOP", course.getDescription());
        assertEquals(teacher, course.getTeacher());
        assertEquals(30, course.getDurationHours());
        assertEquals(DifficultyLevel.BEGINNER, course.getDifficultyLevel());
        assertEquals(1, course.getLessons().size());
        assertTrue(course.getQuizzes().isEmpty());
    }

    @Test
    void buildsCourseWithMultipleLessonsAndQuizzes() {
        Quiz basics = TestData.threeQuestionQuiz("Basics");
        Quiz oop = TestData.threeQuestionQuiz("OOP");

        Course course = validBuilder()
                .addLesson(TestData.lesson("Variables"))
                .addLesson(TestData.lesson("Classes"))
                .addQuiz(basics)
                .addQuiz(oop)
                .build();

        assertEquals(3, course.getLessons().size());
        assertEquals("Classes", course.getLessons().get(2).getTitle());
        assertEquals(2, course.getQuizzes().size());
        assertTrue(course.containsQuiz(basics));
        assertTrue(course.containsQuiz(oop));
    }

    @Test
    void rejectsCourseWithoutRequiredFields() {
        IllegalStateException error = assertThrows(IllegalStateException.class, () -> Course.builder().build());

        String message = error.getMessage();
        assertTrue(message.contains("course id is required"));
        assertTrue(message.contains("title is required"));
        assertTrue(message.contains("teacher is required"));
        assertTrue(message.contains("at least one lesson is required"));
        assertTrue(message.contains("duration is required"));
        assertTrue(message.contains("difficulty level is required"));
    }

    @Test
    void rejectsBlankTitle() {
        assertThrows(IllegalStateException.class, () -> validBuilder().setTitle("   ").build());
    }

    @Test
    void rejectsMissingTeacher() {
        assertThrows(IllegalStateException.class, () -> validBuilder().setTeacher(null).build());
    }

    @Test
    void rejectsCourseWithoutLessons() {
        Course.Builder builder = Course.builder()
                .setId("CRS-2").setTitle("Empty").setTeacher(teacher)
                .setDuration(10).setDifficultyLevel(DifficultyLevel.ADVANCED);

        IllegalStateException error = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(error.getMessage().contains("at least one lesson"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -5, Course.MAX_DURATION_HOURS + 1})
    void rejectsInvalidDuration(int hours) {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> validBuilder().setDuration(hours).build());
        assertTrue(error.getMessage().contains("duration must be between"));
    }

    @Test
    void rejectsUnknownDifficultyLevel() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> validBuilder().setDifficultyLevel("EXPERT"));
        assertTrue(error.getMessage().contains("EXPERT"));
    }

    @Test
    void rejectsDuplicateQuizTitles() {
        Course.Builder builder = validBuilder()
                .addQuiz(TestData.threeQuestionQuiz("Final"))
                .addQuiz(TestData.threeQuestionQuiz("final"));

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void rejectsNullLessonImmediately() {
        assertThrows(NullPointerException.class, () -> validBuilder().addLesson(null));
    }

    @Test
    void builtCourseIsImmutable() {
        Course.Builder builder = validBuilder();
        Course course = builder.build();

        builder.addLesson(TestData.lesson("Added after build"));

        assertEquals(1, course.getLessons().size());
        Lesson extra = TestData.lesson("Extra");
        assertThrows(UnsupportedOperationException.class, () -> course.getLessons().add(extra));
        assertThrows(UnsupportedOperationException.class, () -> course.getQuizzes().clear());
    }
}
