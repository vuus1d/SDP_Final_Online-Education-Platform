package com.eduplatform.console;

import com.eduplatform.model.Course;
import com.eduplatform.model.DifficultyLevel;
import com.eduplatform.model.Lesson;
import com.eduplatform.model.Question;
import com.eduplatform.model.Quiz;
import com.eduplatform.model.Teacher;
import com.eduplatform.service.CourseService;
import com.eduplatform.service.UserService;
import com.eduplatform.facade.EducationPlatformFacade;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Interactive dialog that assembles a course step by step with {@link Course.Builder}. */
public class CourseCreationWizard {

    private static final int MAX_LESSON_MINUTES = 600;
    private static final int MAX_OPTIONS = 6;

    private final ConsoleInput input;
    private final PrintStream out;
    private final UserService userService;
    private final CourseService courseService;
    private final EducationPlatformFacade facade;

    public CourseCreationWizard(ConsoleInput input, PrintStream out, UserService userService, CourseService courseService, EducationPlatformFacade facade) {
        this.input = input;
        this.out = out;
        this.userService = userService;
        this.courseService = courseService;
        this.facade = facade;
    }

    public void run() {
        Optional<Teacher> teacher = input.choose("Select the teacher who creates the course:",
                userService.getTeachers(), Teacher::toString, "No teachers registered. Register a teacher first.");
        if (teacher.isEmpty()) {
            return;
        }

        Course course = facade.createCourse(teacher.get().getId(), builder -> {
            builder.setTitle(input.readNonBlank("Course title: "))
                    .setDescription(input.readLine("Course description: "))
                    .setDuration(input.readInt(String.format("Duration in hours (%d-%d): ", Course.MIN_DURATION_HOURS, Course.MAX_DURATION_HOURS), Course.MIN_DURATION_HOURS, Course.MAX_DURATION_HOURS))
                    .setDifficultyLevel(chooseDifficulty());
            String topics = input.readLine("Topics (comma-separated, optional): ");
            for (String topic : topics.split(",")) if (!topic.isBlank()) builder.addTopic(topic);
            builder.setMinimumPerformance(input.readInt("Minimum readiness score (0-100): ", 0, 100));
            addLessons(builder); addQuizzes(builder);
        });
        out.println("Course created successfully: " + course);
        out.printf("  %d lesson(s), %d quiz(zes)%n", course.getLessons().size(), course.getQuizzes().size());
    }

    private DifficultyLevel chooseDifficulty() {
        return input.choose("Difficulty level:", List.of(DifficultyLevel.values()), DifficultyLevel::name, "")
                .orElseThrow();
    }

    private void addLessons(Course.Builder builder) {
        int lessonNumber = 1;
        do {
            out.println("-- Lesson " + lessonNumber++ + " --");
            builder.addLesson(new Lesson(
                    input.readNonBlank("Lesson title: "),
                    input.readLine("Lesson content: "),
                    input.readInt("Lesson duration in minutes (1-" + MAX_LESSON_MINUTES + "): ", 1, MAX_LESSON_MINUTES)));
        } while (input.readYesNo("Add another lesson?"));
    }

    private void addQuizzes(Course.Builder builder) {
        String prompt = "Add a quiz to this course?";
        while (input.readYesNo(prompt)) {
            builder.addQuiz(readQuiz());
            prompt = "Add another quiz?";
        }
    }

    private Quiz readQuiz() {
        String title = input.readNonBlank("Quiz title: ");
        List<Question> questions = new ArrayList<>();
        do {
            out.println("-- Question " + (questions.size() + 1) + " --");
            questions.add(readQuestion());
        } while (input.readYesNo("Add another question?"));
        return new Quiz(title, questions);
    }

    private Question readQuestion() {
        String text = input.readNonBlank("Question text: ");
        int optionCount = input.readInt("Number of options (" + Question.MIN_OPTIONS + "-" + MAX_OPTIONS + "): ",
                Question.MIN_OPTIONS, MAX_OPTIONS);
        List<String> options = new ArrayList<>();
        for (int i = 1; i <= optionCount; i++) {
            options.add(input.readNonBlank("  Option " + i + ": "));
        }
        int correctOption = input.readInt("Number of the correct option (1-" + optionCount + "): ", 1, optionCount);
        return new Question(text, options, correctOption - 1);
    }
}
