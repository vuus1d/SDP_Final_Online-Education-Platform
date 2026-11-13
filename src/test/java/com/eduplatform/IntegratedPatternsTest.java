package com.eduplatform;

import com.eduplatform.facade.EducationPlatformFacade;
import com.eduplatform.factory.UserFactory;
import com.eduplatform.model.*;
import com.eduplatform.observer.GradeService;
import com.eduplatform.recommendation.*;
import com.eduplatform.service.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class IntegratedPatternsTest {
    @Test void allSixPatternsCoordinateThroughTheProductionFacade() {
        GradeService grades = new GradeService();
        UserService users = new UserService();
        CourseService courses = new CourseService();
        NotificationService notifications = new NotificationService(grades);
        RecommendationService strategy = new RecommendationService(new InterestBasedRecommendationStrategy());
        EducationPlatformFacade facade = new EducationPlatformFacade(users, courses, new QuizService(grades), grades, notifications, strategy);

        Teacher teacher = (Teacher) facade.registerUser(UserRole.TEACHER, "Teacher", "teacher@example.test");
        Student student = (Student) facade.registerUser(UserRole.STUDENT, "Learner", "learner@example.test");
        assertInstanceOf(Admin.class, UserFactory.createUser(UserRole.ADMIN, "Admin", "admin@example.test"));
        facade.subscribe(student);
        facade.setInterests(student.getId(), Set.of("java"));

        Quiz quiz = new Quiz("Java basics", List.of(new Question("Keyword?", List.of("new", "class"), 0)));
        Course course = facade.createCourse(teacher.getId(), builder -> builder.setTitle("Java 21")
                .setDescription("Java course").setDuration(2).setDifficultyLevel(DifficultyLevel.BEGINNER)
                .addTopic("java").setMinimumPerformance(50)
                .addLesson(new Lesson("Lesson 1", "", 30)).addQuiz(quiz));
        assertEquals(1, course.getLessons().size());
        assertEquals(1, course.getQuizzes().size());
        assertEquals(List.of(course), facade.recommend(student.getId(), 5).stream().map(CourseRecommendation::course).toList());
        facade.switchRecommendationStrategy(new PerformanceBasedRecommendationStrategy());
        assertTrue(facade.recommend(student.getId(), 5).isEmpty(), "A new student has 0% recorded performance and does not meet the 50% course threshold");
        assertThrows(IllegalStateException.class, () -> facade.requestCertificate(student.getId(), course.getId()));
        facade.enroll(student.getId(), course.getId());

        facade.addMaterials(course.getId(), List.of(new LearningMaterial("Notes", "notes.pdf")));
        facade.enableCertificate(course.getId());
        assertEquals(course, facade.materials(course.getId()).stream().findFirst().map(ignored -> course).orElseThrow());
        assertTrue(courses.findById(course.getId()).orElseThrow().getTeacher().equals(teacher));

        Grade grade = facade.submitQuiz(student.getId(), course.getId(), quiz.getTitle(), List.of(0));
        assertEquals(100, grade.getScorePercentage());
        assertEquals(1, grades.getGradesForStudent(student).size());
        assertEquals(1, facade.notifications(student.getId()).size());
        assertThrows(IllegalStateException.class, () -> grades.recordGrade(grade), "The same saved grade event cannot be published twice");
        assertEquals(1, facade.notifications(student.getId()).size());
        facade.markLessonCompleted(student.getId(), course.getId(), 1);
        assertEquals(100, facade.progress(student.getId(), course.getId()).completionPercent());
        var first = facade.requestCertificate(student.getId(), course.getId()).orElseThrow();
        var second = facade.requestCertificate(student.getId(), course.getId()).orElseThrow();
        assertEquals(first.certificateId(), second.certificateId());
        assertTrue(facade.recommend(student.getId(), 5).isEmpty(), "Completed courses are filtered");
    }
}
