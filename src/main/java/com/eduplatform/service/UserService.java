package com.eduplatform.service;

import com.eduplatform.factory.UserFactory;
import com.eduplatform.model.Student;
import com.eduplatform.model.Teacher;
import com.eduplatform.model.User;
import com.eduplatform.model.UserRole;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Registers users (always through {@link UserFactory}) and keeps them in memory. */
public class UserService {

    private final Map<String, User> usersById = new LinkedHashMap<>();

    public User registerUser(UserRole role, String name, String email) {
        ensureEmailIsAvailable(email);
        User user = UserFactory.createUser(role, name, email);
        usersById.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public List<User> getAllUsers() {
        return List.copyOf(usersById.values());
    }

    public List<Student> getStudents() {
        return usersOfType(Student.class);
    }

    public List<Teacher> getTeachers() {
        return usersOfType(Teacher.class);
    }

    public <T extends User> List<T> usersOfType(Class<T> type) {
        return usersById.values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toUnmodifiableList());
    }

    public boolean isEmailRegistered(String email) {
        if (email == null) {
            return false;
        }
        String candidate = email.trim();
        return usersById.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(candidate));
    }

    private void ensureEmailIsAvailable(String email) {
        if (isEmailRegistered(email)) {
            throw new IllegalStateException("A user with email '" + email.trim() + "' is already registered");
        }
    }
}
