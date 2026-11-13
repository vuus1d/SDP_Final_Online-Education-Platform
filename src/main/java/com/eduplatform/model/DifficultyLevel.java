package com.eduplatform.model;

public enum DifficultyLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    public static DifficultyLevel fromString(String value) {
        return EnumParser.parse(DifficultyLevel.class, value, "Difficulty level");
    }
}
