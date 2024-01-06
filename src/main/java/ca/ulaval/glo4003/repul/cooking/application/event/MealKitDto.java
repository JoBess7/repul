package ca.ulaval.glo4003.repul.cooking.application.event;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public record MealKitDto(MealKitUniqueIdentifier mealKitId, boolean isToBeDelivered) {
}
