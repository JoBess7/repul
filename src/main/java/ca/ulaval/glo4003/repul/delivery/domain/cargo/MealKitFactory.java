package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;

public class MealKitFactory {
    public MealKit createMealKit(DeliveryLocation deliveryLocation, MealKitUniqueIdentifier mealKitId) {
        return new MealKit(deliveryLocation, mealKitId, DeliveryStatus.PENDING);
    }
}
