package ca.ulaval.glo4003.repul.subscription.application.query;

import java.time.DayOfWeek;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidDayOfWeekException;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;

public record SubscriptionQuery(
    DeliveryLocationId deliveryLocationId,
    DayOfWeek dayOfWeek,
    MealKitType mealKitType
) {
    public SubscriptionQuery(DeliveryLocationId deliveryLocationId, DayOfWeek dayOfWeek, String mealKitType) {
        this(deliveryLocationId, dayOfWeek, MealKitType.from(mealKitType));
    }

    public static SubscriptionQuery from(String deliveryLocationId, String dayOfWeek, String mealKitType) {
        try {
            DayOfWeek.valueOf(dayOfWeek);
        } catch (IllegalArgumentException e) {
            throw new InvalidDayOfWeekException();
        }
        try {
            DeliveryLocationId.valueOf(deliveryLocationId);
        } catch (IllegalArgumentException e) {
            throw new InvalidLocationIdException();
        }
        return new SubscriptionQuery(DeliveryLocationId.valueOf(deliveryLocationId), DayOfWeek.valueOf(dayOfWeek), mealKitType);
    }
}
