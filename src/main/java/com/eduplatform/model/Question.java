package com.eduplatform.model;

import java.util.List;

public final class Question {

    public static final int MIN_OPTIONS = 2;

    private final String text;
    private final List<String> options;
    private final int correctOptionIndex;

    /**
     * @param correctOptionIndex zero-based index of the correct option
     */
    public Question(String text, List<String> options, int correctOptionIndex) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Question text must not be empty");
        }
        if (options == null || options.size() < MIN_OPTIONS) {
            throw new IllegalArgumentException("A question needs at least " + MIN_OPTIONS + " options");
        }
        if (options.stream().anyMatch(option -> option == null || option.isBlank())) {
            throw new IllegalArgumentException("Question options must not be empty");
        }
        if (correctOptionIndex < 0 || correctOptionIndex >= options.size()) {
            throw new IllegalArgumentException("Correct option index is out of range");
        }
        this.text = text.trim();
        this.options = List.copyOf(options);
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean isCorrect(int selectedOptionIndex) {
        return selectedOptionIndex == correctOptionIndex;
    }
}
