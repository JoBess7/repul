package ca.ulaval.glo4003.repul.commons.domain.exception;

import java.util.Arrays;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;

public class InvalidMealKitTypeException extends CommonException {
    public InvalidMealKitTypeException() {
        super("The meal kit type must be one of the following: " + Arrays.toString(MealKitType.values()));
    }
}
