package ca.ulaval.glo4003.repul.subscription.domain;

import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;

public interface SubscriberRepository {
    void save(Subscriber subscriber);

    Subscriber getById(SubscriberUniqueIdentifier subscriberId);

    boolean cardNumberExists(UserCardNumber cardNumber);
}
