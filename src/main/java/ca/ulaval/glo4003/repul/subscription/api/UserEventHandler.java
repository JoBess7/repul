package ca.ulaval.glo4003.repul.subscription.api;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.user.application.event.UserCreatedEvent;

import com.google.common.eventbus.Subscribe;

public class UserEventHandler {
    private final SubscriberService subscriberService;

    public UserEventHandler(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Subscribe
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        SubscriberUniqueIdentifier subscriberUniqueIdentifier =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(userCreatedEvent.userId);
        subscriberService.createSubscriber(subscriberUniqueIdentifier, userCreatedEvent.idul, userCreatedEvent.name, userCreatedEvent.birthdate,
            userCreatedEvent.gender, userCreatedEvent.email);
    }
}
