package com.eduplatform.factory;

import com.eduplatform.model.Admin;
import com.eduplatform.model.Student;
import com.eduplatform.model.Teacher;
import com.eduplatform.model.User;
import com.eduplatform.model.UserRole;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory pattern: the single place where {@link User} objects are created.
 * Callers ask for a role and receive the matching concrete user type.
 */
public final class UserFactory {

    private static final AtomicInteger ID_SEQUENCE = new AtomicInteger();

    private UserFactory() {
    }

    public static User createUser(UserRole role, String name, String email) {
        if (role == null) {
            throw new IllegalArgumentException("User role must be specified. Supported roles: " + UserRole.supportedRoles());
        }
        String id = nextId(role);
        String normalizedName = name == null ? null : name.trim();
        String normalizedEmail = email == null ? null : email.trim().toLowerCase(Locale.ROOT);

        return switch (role) {
            case STUDENT -> new Student(id, normalizedName, normalizedEmail);
            case TEACHER -> new Teacher(id, normalizedName, normalizedEmail);
            case ADMIN -> new Admin(id, normalizedName, normalizedEmail);
        };
    }

    /** Creates a user from a textual role such as "student"; rejects unknown roles. */
    public static User createUser(String roleName, String name, String email) {
        return createUser(UserRole.fromString(roleName), name, email);
    }

    public static Student createStudent(String name, String email) {
        return (Student) createUser(UserRole.STUDENT, name, email);
    }

    public static Teacher createTeacher(String name, String email) {
        return (Teacher) createUser(UserRole.TEACHER, name, email);
    }

    public static Admin createAdmin(String name, String email) {
        return (Admin) createUser(UserRole.ADMIN, name, email);
    }

    private static String nextId(UserRole role) {
        return role.getIdPrefix() + "-" + ID_SEQUENCE.incrementAndGet();
    }
}
