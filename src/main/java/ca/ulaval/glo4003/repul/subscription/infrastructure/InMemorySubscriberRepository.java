package ca.ulaval.glo4003.repul.subscription.infrastructure;

import java.util.HashMap;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SubscriberNotFoundException;

public class InMemorySubscriberRepository implements SubscriberRepository {
    private final Map<SubscriberUniqueIdentifier, Subscriber> subscribers = new HashMap<>();

    @Override
    public void save(Subscriber subscriber) {
        subscribers.put(subscriber.getSubscriberId(), subscriber);
    }

    @Override
    public Subscriber getById(SubscriberUniqueIdentifier subscriberId) {
        Subscriber subscriber = subscribers.get(subscriberId);

        if (subscriber == null) {
            throw new SubscriberNotFoundException();
        }

        return subscriber;
    }

    @Override
    public boolean cardNumberExists(UserCardNumber cardNumber) {
        return subscribers.values().stream()
            .anyMatch(subscriber -> subscriber.getCardNumber().isPresent() && subscriber.getCardNumber().get().equals(cardNumber));
    }
}
