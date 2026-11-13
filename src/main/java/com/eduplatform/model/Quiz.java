package com.eduplatform.model;

import java.util.List;

public final class Quiz {

    private final String title;
    private final List<Question> questions;

    public Quiz(String title, List<Question> questions) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Quiz title must not be empty");
        }
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("A quiz needs at least one question");
        }
        this.title = title.trim();
        this.questions = List.copyOf(questions);
    }

    public String getTitle() {
        return title;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getQuestionCount() {
        return questions.size();
    }

    /**
     * @param answers zero-based selected option per question, in question order
     */
    public int countCorrectAnswers(List<Integer> answers) {
        if (answers == null || answers.size() != questions.size()) {
            throw new IllegalArgumentException("Expected " + questions.size() + " answer(s) for quiz '" + title + "'");
        }
        int correct = 0;
        for (int i = 0; i < questions.size(); i++) {
            Integer answer = answers.get(i);
            if (answer != null && questions.get(i).isCorrect(answer)) {
                correct++;
            }
        }
        return correct;
    }

    @Override
    public String toString() {
        return title + " (" + questions.size() + " question(s))";
    }
}
