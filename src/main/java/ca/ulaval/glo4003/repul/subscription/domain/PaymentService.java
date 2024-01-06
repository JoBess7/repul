package ca.ulaval.glo4003.repul.subscription.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.Amount;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;

public abstract class PaymentService {
    private final Map<MealKitType, Amount> mealKitPrices;

    public PaymentService() {
        this.mealKitPrices = new HashMap<>();
        mealKitPrices.put(MealKitType.STANDARD, new Amount(75));
    }

    public abstract void pay(SubscriberUniqueIdentifier subscriberId, MealKitType mealKitType, LocalDate deliveryDate);

    protected Amount getMealKitPrice(MealKitType mealKitType) {
        return mealKitPrices.get(mealKitType);
    }
}
