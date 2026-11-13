package com.eduplatform;

import com.eduplatform.model.Lesson;
import com.eduplatform.model.Question;
import com.eduplatform.model.Quiz;

import java.util.List;

/** Small reusable fixtures for tests. */
public final class TestData {

    private TestData() {
    }

    public static Lesson lesson(String title) {
        return new Lesson(title, "Content of " + title, 45);
    }

    /** Quiz with three questions whose correct answers are options 0, 1 and 0. */
    public static Quiz threeQuestionQuiz(String title) {
        return new Quiz(title, List.of(
                new Question("Q1", List.of("right", "wrong"), 0),
                new Question("Q2", List.of("wrong", "right"), 1),
                new Question("Q3", List.of("right", "wrong", "wrong"), 0)));
    }

    public static List<Integer> allCorrectAnswers() {
        return List.of(0, 1, 0);
    }
}
