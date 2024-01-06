package ca.ulaval.glo4003.repul.subscription.infrastructure;

import java.time.LocalDate;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;

public class LogPaymentService extends PaymentService {
    public LogPaymentService() {
        super();
    }

    @Override
    public void pay(SubscriberUniqueIdentifier subscriberId, MealKitType mealKitType, LocalDate deliveryDate) {
        String message = String.format("The account with id %s has been debited $%s for a meal kit of type %s to be delivered on %s",
            subscriberId.getUUID().toString(),
            getMealKitPrice(mealKitType).getValue(),
            mealKitType.toString(),
            deliveryDate.toString()
        );

        System.out.println(message);
    }
}
