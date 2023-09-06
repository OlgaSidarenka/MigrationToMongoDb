package ua.epam.mishchenko.ticketbooking.model;

import java.util.Arrays;

public enum Category {
    PREMIUM,
    STANDARD,
    BAR;

    public static Category fromString(String value) {
        return Arrays.stream(Category.values()).filter(category -> value.equals(category.name())).findFirst().orElseThrow();
    }
}
