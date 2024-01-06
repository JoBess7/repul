package ca.ulaval.glo4003.repul.commons.domain;

import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidMealKitTypeException;

public enum MealKitType {
    STANDARD;

    public static MealKitType from(String type) {
        try {
            return MealKitType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidMealKitTypeException();
        }
    }
}
