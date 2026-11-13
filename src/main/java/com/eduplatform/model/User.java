package com.eduplatform.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Base type for every platform user. Concrete users are created through
 * {@link com.eduplatform.factory.UserFactory}.
 */
public abstract class User {

    public static final int MAX_NAME_LENGTH = 50;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final String id;
    private final String name;
    private final String email;

    protected User(String id, String name, String email) {
        this.id = requireText(id, "User id");
        this.name = validateName(name);
        this.email = validateEmail(email);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public abstract UserRole getRole();

    /** Role-specific short summary of what this user currently has on the platform. */
    public abstract String getDashboardSummary();

    private static String validateName(String name) {
        String value = requireText(name, "User name");
        if (value.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("User name must not be longer than " + MAX_NAME_LENGTH + " characters");
        }
        return value;
    }

    private static String validateEmail(String email) {
        String value = requireText(email, "Email");
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email address: '" + value + "'");
        }
        return value;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be empty");
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof User)) {
            return false;
        }
        return id.equals(((User) other).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s <%s>", id, getRole(), name, email);
    }
}
