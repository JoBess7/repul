package ca.ulaval.glo4003.repul.subscription.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;

public class UserCardAddedEvent extends RepULEvent {
    public final SubscriberUniqueIdentifier subscriberId;
    public final UserCardNumber userCardNumber;

    public UserCardAddedEvent(SubscriberUniqueIdentifier subscriberId, UserCardNumber userCardNumber) {
        super();
        this.subscriberId = subscriberId;
        this.userCardNumber = userCardNumber;
    }
}
