package com.eduplatform.service;

import com.eduplatform.model.Course;
import com.eduplatform.model.Grade;
import com.eduplatform.model.Quiz;
import com.eduplatform.model.Student;
import com.eduplatform.observer.GradeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Evaluates quiz attempts and hands the resulting grade to {@link GradeService} for publishing. */
public class QuizService {

    private final GradeService gradeService;

    public QuizService(GradeService gradeService) {
        this.gradeService = Objects.requireNonNull(gradeService, "Grade service must not be null");
    }

    /**
     * @param answers zero-based selected option per question, in question order
     */
    public Grade submitQuiz(Student student, Course course, Quiz quiz, List<Integer> answers) {
        Objects.requireNonNull(student, "Student must not be null");
        Objects.requireNonNull(course, "Course must not be null");
        Objects.requireNonNull(quiz, "Quiz must not be null");
        if (!student.isEnrolledIn(course)) {
            throw new IllegalStateException(student.getName() + " is not enrolled in '" + course.getTitle() + "'");
        }
        int correctAnswers = quiz.countCorrectAnswers(answers);
        Grade grade = new Grade(student, course, quiz, correctAnswers, LocalDateTime.now());
        gradeService.recordGrade(grade);
        student.markQuizCompleted(course, quiz, grade);
        return grade;
    }
}
