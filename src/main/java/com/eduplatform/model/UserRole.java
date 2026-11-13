package com.eduplatform.model;

public enum UserRole {
    STUDENT("STU"),
    TEACHER("TCH"),
    ADMIN("ADM");

    private final String idPrefix;

    UserRole(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public static UserRole fromString(String value) {
        return EnumParser.parse(UserRole.class, value, "User role");
    }

    public static String supportedRoles() {
        return EnumParser.supportedValues(UserRole.class);
    }
}
