package com.eduplatform.factory;

import com.eduplatform.model.Admin;
import com.eduplatform.model.User;
import com.eduplatform.model.UserRole;

import java.util.List;

/**
 * Standalone demonstration of the Factory pattern using the production {@link UserFactory}.
 */
public final class FactoryDemo {

    private FactoryDemo() {
    }

    public static void main(String[] args) {
        System.out.println("=== FACTORY PATTERN DEMO: user creation ===");

        User student = UserFactory.createUser(UserRole.STUDENT, "Nurasyl", "nurasyl@example.com");
        User teacher = UserFactory.createUser(UserRole.TEACHER, "John Smith", "John@Example.com");
        User admin = UserFactory.createUser("admin", "Platform Admin", "admin@eduplatform.com");

        List<User> users = List.of(student, teacher, admin);
        System.out.println("The factory returned a different concrete class for each role:");
        for (User user : users) {
            printUser(user);
        }

        System.out.println();
        System.out.println("Role-specific behavior (Admin counts users by role):");
        ((Admin) admin).countUsersByRole(users)
                .forEach((role, count) -> System.out.println("  " + role + ": " + count));

        System.out.println();
        System.out.println("--- Validation: invalid roles and data are rejected ---");
        tryCreate("GUEST", "Visitor", "visitor@example.com");
        tryCreate("STUDENT", "   ", "blank@example.com");
        tryCreate("TEACHER", "Bob", "not-an-email");
    }

    private static void printUser(User user) {
        System.out.println("  " + user.getClass().getSimpleName());
        System.out.println("    ID:      " + user.getId());
        System.out.println("    Role:    " + user.getRole());
        System.out.println("    Name:    " + user.getName());
        System.out.println("    Email:   " + user.getEmail());
        System.out.println("    Summary: " + user.getDashboardSummary());
    }

    private static void tryCreate(String role, String name, String email) {
        try {
            UserFactory.createUser(role, name, email);
            System.out.println("  Unexpectedly created user for role '" + role + "'");
        } catch (IllegalArgumentException e) {
            System.out.println("  Rejected (" + role + ", '" + name + "', " + email + "): " + e.getMessage());
        }
    }
}
