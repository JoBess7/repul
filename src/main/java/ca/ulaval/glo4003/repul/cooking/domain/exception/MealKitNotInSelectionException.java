package ca.ulaval.glo4003.repul.cooking.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public class MealKitNotInSelectionException extends CookingException {
    public MealKitNotInSelectionException(MealKitUniqueIdentifier mealKitId) {
        super("The meal kit with id " + mealKitId.getUUID().toString() + " is not in your selection.");
    }
}
