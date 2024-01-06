package ca.ulaval.glo4003.repul.subscription.domain;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;

public interface SubscriptionRepository {
    void save(Subscription subscription);

    Subscription getById(SubscriptionUniqueIdentifier subscriptionId);

    List<Subscription> getAll();

    List<Subscription> getBySubscriberId(SubscriberUniqueIdentifier subscriberId);

    Subscription getSubscriptionByOrderId(MealKitUniqueIdentifier orderId);
}
