package ca.ulaval.glo4003.repul.subscription.application.payload;

import java.time.LocalDate;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.Frequency;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;

public record SubscriptionPayload(SubscriptionUniqueIdentifier subscriptionId, Frequency frequency, DeliveryLocationId deliveryLocationId, LocalDate startDate,
                                  MealKitType mealKitType, Semester semester) {

    public static SubscriptionPayload from(Subscription subscription) {
        return new SubscriptionPayload(subscription.getSubscriptionId(), subscription.getFrequency(), subscription.getDeliveryLocationId(),
            subscription.getStartDate(), subscription.getMealKitType(), subscription.getSemester());
    }
}
