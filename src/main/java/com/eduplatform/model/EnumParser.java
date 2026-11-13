package com.eduplatform.model;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

final class EnumParser {

    private EnumParser() {
    }

    static <E extends Enum<E>> E parse(Class<E> enumType, String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " must not be empty. Supported values: " + supportedValues(enumType));
        }
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported " + label.toLowerCase(Locale.ROOT) + ": '" + value.trim()
                    + "'. Supported values: " + supportedValues(enumType), e);
        }
    }

    static <E extends Enum<E>> String supportedValues(Class<E> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
