package com.eduplatform.model;

public final class Lesson {

    private final String title;
    private final String content;
    private final int durationMinutes;

    public Lesson(String title, String content, int durationMinutes) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Lesson title must not be empty");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Lesson duration must be a positive number of minutes");
        }
        this.title = title.trim();
        this.content = content == null ? "" : content.trim();
        this.durationMinutes = durationMinutes;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    public String toString() {
        return title + " (" + durationMinutes + " min)";
    }
}
