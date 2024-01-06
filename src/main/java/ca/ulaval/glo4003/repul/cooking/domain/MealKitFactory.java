package ca.ulaval.glo4003.repul.cooking.domain;

import java.time.LocalDate;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;

public class MealKitFactory {
    public MealKit createMealKit(MealKitUniqueIdentifier mealKitId, SubscriberUniqueIdentifier subscriberId,
                                 MealKitType type, LocalDate deliveryDate, Optional<DeliveryLocationId> deliveryLocationId) {
        boolean isToBeDelivered = deliveryLocationId.isPresent();
        return new MealKit(mealKitId, subscriberId, deliveryDate, RecipesCatalog.getInstance().getRecipes(type), isToBeDelivered);
    }
}
