package ca.ulaval.glo4003.repul.cooking.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public class MealKitNotFoundException extends CookingException {
    public MealKitNotFoundException(MealKitUniqueIdentifier mealKitId) {
        super("Meal kit with id " + mealKitId.getUUID().toString() + " not found.");
    }
}
