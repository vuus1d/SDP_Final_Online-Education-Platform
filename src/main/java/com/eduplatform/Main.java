package com.eduplatform;

import com.eduplatform.console.ConsoleInput;
import com.eduplatform.console.ConsoleMenu;
import com.eduplatform.observer.GradeService;
import com.eduplatform.service.CourseService;
import com.eduplatform.service.NotificationService;
import com.eduplatform.service.QuizService;
import com.eduplatform.service.UserService;

import java.util.Scanner;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        GradeService gradeService = new GradeService();
        UserService userService = new UserService();
        CourseService courseService = new CourseService();
        QuizService quizService = new QuizService(gradeService);
        NotificationService notificationService = new NotificationService(gradeService);

        ConsoleInput input = new ConsoleInput(new Scanner(System.in), System.out);
        new ConsoleMenu(input, System.out, userService, courseService, quizService, gradeService, notificationService)
                .run();
    }
}
