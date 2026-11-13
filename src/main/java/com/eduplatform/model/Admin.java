package com.eduplatform.model;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class Admin extends User {

    public Admin(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }

    @Override
    public String getDashboardSummary() {
        return "Administers platform users and courses";
    }

    /** Builds a per-role head count of the given users, used for the admin platform overview. */
    public Map<UserRole, Long> countUsersByRole(Collection<? extends User> users) {
        Objects.requireNonNull(users, "Users must not be null");
        Map<UserRole, Long> counts = new EnumMap<>(UserRole.class);
        for (UserRole role : UserRole.values()) {
            counts.put(role, 0L);
        }
        users.forEach(user -> counts.merge(user.getRole(), 1L, Long::sum));
        return counts;
    }
}
