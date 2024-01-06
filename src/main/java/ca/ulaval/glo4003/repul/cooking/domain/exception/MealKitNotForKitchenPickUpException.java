package ca.ulaval.glo4003.repul.cooking.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public class MealKitNotForKitchenPickUpException extends CookingException {
    public MealKitNotForKitchenPickUpException(MealKitUniqueIdentifier mealKitId) {
        super("Meal kit with id " + mealKitId.getUUID().toString() + " not for kitchen pick up.");
    }
}
