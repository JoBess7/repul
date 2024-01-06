package ca.ulaval.glo4003.repul.subscription.application;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.application.event.UserCardAddedEvent;
import ca.ulaval.glo4003.repul.subscription.application.exception.CardNumberAlreadyInUseException;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;

public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SubscriberFactory subscriberFactory;
    private final RepULEventBus eventBus;

    public SubscriberService(SubscriberRepository subscriberRepository, SubscriberFactory subscriberFactory, RepULEventBus eventBus) {
        this.subscriberRepository = subscriberRepository;
        this.subscriberFactory = subscriberFactory;
        this.eventBus = eventBus;
    }

    public void createSubscriber(SubscriberUniqueIdentifier subscriberId, IDUL idul, Name name, Birthdate birthdate, Gender gender, Email email) {
        Subscriber subscriber = subscriberFactory.createSubscriber(subscriberId, idul, name, birthdate, gender, email);

        subscriberRepository.save(subscriber);
    }

    public ProfilePayload getSubscriberProfile(SubscriberUniqueIdentifier subscriberId) {
        Subscriber subscriber = subscriberRepository.getById(subscriberId);

        return ProfilePayload.from(subscriber.getProfile());
    }

    public void addCard(SubscriberUniqueIdentifier subscriberUniqueIdentifier, UserCardNumber cardNumber) {
        Subscriber subscriber = subscriberRepository.getById(subscriberUniqueIdentifier);

        validateCardNumber(cardNumber);
        subscriber.setCardNumber(cardNumber);

        subscriberRepository.save(subscriber);
        eventBus.publish(new UserCardAddedEvent(subscriberUniqueIdentifier, cardNumber));
    }

    private void validateCardNumber(UserCardNumber cardNumber) {
        if (subscriberRepository.cardNumberExists(cardNumber)) {
            throw new CardNumberAlreadyInUseException();
        }
    }
}
