package com.eduplatform;

import com.eduplatform.factory.UserFactory;
import com.eduplatform.model.Admin;
import com.eduplatform.model.Student;
import com.eduplatform.model.Teacher;
import com.eduplatform.model.User;
import com.eduplatform.model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserFactoryTest {

    @Test
    void createsStudent() {
        User user = UserFactory.createUser(UserRole.STUDENT, "Nurasyl", "nurasyl@example.com");

        assertInstanceOf(Student.class, user);
        assertEquals(UserRole.STUDENT, user.getRole());
        assertEquals("Nurasyl", user.getName());
        assertEquals("nurasyl@example.com", user.getEmail());
        assertTrue(user.getId().startsWith("STU-"));
    }

    @Test
    void createsTeacher() {
        User user = UserFactory.createUser(UserRole.TEACHER, "John", "john@example.com");

        assertInstanceOf(Teacher.class, user);
        assertEquals(UserRole.TEACHER, user.getRole());
        assertTrue(user.getId().startsWith("TCH-"));
    }

    @Test
    void createsAdmin() {
        User user = UserFactory.createUser(UserRole.ADMIN, "Root", "admin@example.com");

        assertInstanceOf(Admin.class, user);
        assertEquals(UserRole.ADMIN, user.getRole());
        assertTrue(user.getId().startsWith("ADM-"));
    }

    @Test
    void createsUserFromRoleNameIgnoringCase() {
        assertInstanceOf(Teacher.class, UserFactory.createUser(" teacher ", "Anna", "anna@example.com"));
    }

    @Test
    void assignsUniqueIds() {
        User first = UserFactory.createStudent("A", "a@example.com");
        User second = UserFactory.createStudent("B", "b@example.com");

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    void normalizesNameAndEmail() {
        User user = UserFactory.createUser(UserRole.STUDENT, "  Aigerim  ", "  Aigerim@Example.COM ");

        assertEquals("Aigerim", user.getName());
        assertEquals("aigerim@example.com", user.getEmail());
    }

    @Test
    void eachRoleHasItsOwnDashboardSummary() {
        assertTrue(UserFactory.createStudent("S", "s@example.com").getDashboardSummary().contains("Enrolled in 0"));
        assertTrue(UserFactory.createTeacher("T", "t@example.com").getDashboardSummary().contains("Teaching 0"));
        assertTrue(UserFactory.createAdmin("A", "ad@example.com").getDashboardSummary().contains("Administers"));
    }

    @Test
    void rejectsNullRole() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> UserFactory.createUser((UserRole) null, "X", "x@example.com"));
        assertTrue(error.getMessage().contains("Supported roles"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"GUEST", "superuser", "123"})
    void rejectsUnsupportedRoleName(String roleName) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> UserFactory.createUser(roleName, "X", "x@example.com"));
        assertTrue(error.getMessage().contains("Unsupported user role"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsMissingName(String name) {
        assertThrows(IllegalArgumentException.class,
                () -> UserFactory.createUser(UserRole.STUDENT, name, "x@example.com"));
    }

    @Test
    void rejectsTooLongName() {
        String longName = "a".repeat(User.MAX_NAME_LENGTH + 1);
        assertThrows(IllegalArgumentException.class,
                () -> UserFactory.createUser(UserRole.STUDENT, longName, "x@example.com"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"not-an-email", "user@", "@example.com", "user@example", "a b@example.com"})
    void rejectsInvalidEmail(String email) {
        assertThrows(IllegalArgumentException.class,
                () -> UserFactory.createUser(UserRole.TEACHER, "John", email));
    }
}
