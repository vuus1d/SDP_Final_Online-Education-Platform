package edu.platform.recommendation;

import edu.platform.domain.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class RecommendationStrategyTest {
    private final StudentProfile student = new StudentProfile(new StudentId("s-1"), Set.of("Java", "Backend"), 40, Set.of());
    private final Course javaBackend = course("java", "Java Backend", Set.of("Java", "Backend"), 20, true);
    private final Course backend = course("backend", "Backend Basics", Set.of("Backend"), 30, true);
    private final Course design = course("design", "Design Studio", Set.of("Design"), 40, true);

    @Test void interestStrategyRanksByOverlapAndIsStable() {
        var strategy = new InterestBasedRecommendationStrategy();
        Course backendAdvanced = course("backend-advanced", "Backend Advanced", Set.of("Backend"), 30, true);
        var first = strategy.recommend(student, List.of(backendAdvanced, backend, javaBackend, design), 10);
        var second = strategy.recommend(student, List.of(design, javaBackend, backend, backendAdvanced), 10);
        assertEquals(List.of("java", "backend-advanced", "backend"), first.stream().map(r -> r.course().id().value()).toList());
        assertEquals(first, second);
        assertEquals(100, first.getFirst().score());
    }

    @Test void interestStrategyHandlesEmptyNoMatchUnavailableAndCompleted() {
        var strategy = new InterestBasedRecommendationStrategy();
        assertTrue(strategy.recommend(student, List.of(), 5).isEmpty());
        assertTrue(strategy.recommend(student, List.of(design), 5).isEmpty());
        StudentProfile completed = new StudentProfile(student.id(), student.interests(), student.performanceScore(), Set.of(javaBackend.id()));
        assertTrue(strategy.recommend(completed, List.of(javaBackend), 5).isEmpty());
        assertTrue(strategy.recommend(student, List.of(course("off", "Offline", Set.of("Java"), 0, false)), 5).isEmpty());
        assertTrue(strategy.recommend(student, List.of(javaBackend), 0).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> strategy.recommend(student, List.of(javaBackend), -1));
    }

    @Test void performanceStrategyRecommendsOnlyCoursesMeetingReadinessAndRanksClosestChallenge() {
        var strategy = new PerformanceBasedRecommendationStrategy();
        Course easy = course("easy", "Easy", Set.of("Other"), 10, true);
        Course fit = course("fit", "Fit", Set.of("Other"), 40, true);
        Course locked = course("locked", "Locked", Set.of("Other"), 41, true);
        var results = strategy.recommend(student, List.of(easy, locked, fit), 10);
        assertEquals(List.of("fit", "easy"), results.stream().map(r -> r.course().id().value()).toList());
        assertEquals(100, results.getFirst().score());
        assertTrue(strategy.recommend(student, List.of(locked), 4).isEmpty());
        assertTrue(strategy.recommend(student, List.of(), 4).isEmpty());
    }

    @Test void recommendationServiceSwitchesStrategyAtRuntimeForTheSameStudent() {
        var service = new RecommendationService(new InterestBasedRecommendationStrategy());
        List<Course> catalog = List.of(javaBackend, design);
        assertEquals("java", service.recommend(student, catalog, 5).getFirst().course().id().value());
        service.setStrategy(new PerformanceBasedRecommendationStrategy());
        assertEquals("design", service.recommend(student, catalog, 5).getFirst().course().id().value());
        assertEquals("Performance-based", service.strategy().name());
        assertThrows(NullPointerException.class, () -> service.setStrategy(null));
    }

    @Test void strategiesUseEachStudentsOwnData() {
        StudentProfile another = new StudentProfile(new StudentId("s-2"), Set.of("Design"), 15, Set.of());
        var strategy = new InterestBasedRecommendationStrategy();
        assertEquals("design", strategy.recommend(another, List.of(javaBackend, design), 1).getFirst().course().id().value());
        assertEquals("java", strategy.recommend(student, List.of(javaBackend, design), 1).getFirst().course().id().value());
    }

    private static Course course(String id, String title, Set<String> topics, int minimum, boolean available) {
        return new Course(new CourseId(id), title, title, topics, minimum, available);
    }
}
