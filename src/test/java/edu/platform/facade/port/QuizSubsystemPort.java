package edu.platform.facade.port;

import edu.platform.domain.CourseId;
import edu.platform.domain.QuizResult;
import edu.platform.domain.StudentId;
import java.util.Map;

@FunctionalInterface
public interface QuizSubsystemPort {
    QuizResult submit(StudentId studentId, CourseId courseId, String quizId, Map<String, String> answers);
}
