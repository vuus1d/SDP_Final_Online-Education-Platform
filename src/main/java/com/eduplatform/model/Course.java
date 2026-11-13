package com.eduplatform.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collections;

/**
 * Immutable educational course. Instances can only be created through {@link Course.Builder}
 * (Builder pattern), which validates every required field before construction.
 */
public final class Course {

    public static final int MIN_DURATION_HOURS = 1;
    public static final int MAX_DURATION_HOURS = 1000;

    private final String id;
    private final String title;
    private final String description;
    private final Teacher teacher;
    private final List<Lesson> lessons;
    private final List<Quiz> quizzes;
    private final int durationHours;
    private final DifficultyLevel difficultyLevel;
    private final Set<String> topics;
    private final int minimumPerformance;
    private final boolean available;

    private Course(Builder builder) {
        this.id = builder.id.trim();
        this.title = builder.title.trim();
        this.description = builder.description == null ? "" : builder.description.trim();
        this.teacher = builder.teacher;
        this.lessons = List.copyOf(builder.lessons);
        this.quizzes = List.copyOf(builder.quizzes);
        this.durationHours = builder.durationHours;
        this.difficultyLevel = builder.difficultyLevel;
        this.topics = Collections.unmodifiableSet(new LinkedHashSet<>(builder.topics));
        this.minimumPerformance = builder.minimumPerformance;
        this.available = builder.available;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public Set<String> getTopics() { return topics; }
    public int getMinimumPerformance() { return minimumPerformance; }
    public boolean isAvailable() { return available; }

    public boolean containsQuiz(Quiz quiz) {
        return quizzes.contains(quiz);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Course)) {
            return false;
        }
        return id.equals(((Course) other).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s %s [%s, %d h] by %s", id, title, difficultyLevel, durationHours, teacher.getName());
    }

    public static final class Builder {

        private String id;
        private String title;
        private String description;
        private Teacher teacher;
        private final List<Lesson> lessons = new ArrayList<>();
        private final List<Quiz> quizzes = new ArrayList<>();
        private Integer durationHours;
        private DifficultyLevel difficultyLevel;
        private final Set<String> topics = new LinkedHashSet<>();
        private int minimumPerformance;
        private boolean available = true;

        private Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setTeacher(Teacher teacher) {
            this.teacher = teacher;
            return this;
        }

        public Builder addLesson(Lesson lesson) {
            lessons.add(Objects.requireNonNull(lesson, "Lesson must not be null"));
            return this;
        }

        public Builder addQuiz(Quiz quiz) {
            quizzes.add(Objects.requireNonNull(quiz, "Quiz must not be null"));
            return this;
        }

        public Builder setDuration(int hours) {
            this.durationHours = hours;
            return this;
        }

        public Builder setDifficultyLevel(DifficultyLevel difficultyLevel) {
            this.difficultyLevel = difficultyLevel;
            return this;
        }

        public Builder setDifficultyLevel(String difficultyLevel) {
            return setDifficultyLevel(DifficultyLevel.fromString(difficultyLevel));
        }

        public Builder addTopic(String topic) {
            if (topic == null || topic.isBlank()) throw new IllegalArgumentException("Topic must not be blank");
            topics.add(topic.trim().toLowerCase(Locale.ROOT));
            return this;
        }
        public Builder setMinimumPerformance(int score) {
            if (score < 0 || score > 100) throw new IllegalArgumentException("Minimum performance must be between 0 and 100");
            minimumPerformance = score;
            return this;
        }
        public Builder setAvailable(boolean value) { available = value; return this; }

        /**
         * Validates the collected data and creates the course.
         *
         * @throws IllegalStateException listing every validation problem found
         */
        public Course build() {
            List<String> errors = validate();
            if (!errors.isEmpty()) {
                throw new IllegalStateException("Cannot build course: " + String.join("; ", errors));
            }
            return new Course(this);
        }

        private List<String> validate() {
            List<String> errors = new ArrayList<>();
            if (isBlank(id)) {
                errors.add("course id is required");
            }
            if (isBlank(title)) {
                errors.add("title is required");
            }
            if (teacher == null) {
                errors.add("teacher is required");
            }
            if (lessons.isEmpty()) {
                errors.add("at least one lesson is required");
            }
            if (durationHours == null) {
                errors.add("duration is required");
            } else if (durationHours < MIN_DURATION_HOURS || durationHours > MAX_DURATION_HOURS) {
                errors.add("duration must be between " + MIN_DURATION_HOURS + " and " + MAX_DURATION_HOURS + " hours");
            }
            if (difficultyLevel == null) {
                errors.add("difficulty level is required");
            }
            if (hasDuplicateQuizTitles()) {
                errors.add("quiz titles must be unique within a course");
            }
            return errors;
        }

        private boolean hasDuplicateQuizTitles() {
            Set<String> titles = new HashSet<>();
            return quizzes.stream().anyMatch(quiz -> !titles.add(quiz.getTitle().toLowerCase(Locale.ROOT)));
        }

        private static boolean isBlank(String value) {
            return value == null || value.isBlank();
        }
    }
}
