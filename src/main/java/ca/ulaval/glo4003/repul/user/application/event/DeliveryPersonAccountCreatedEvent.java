package ca.ulaval.glo4003.repul.user.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;

public class DeliveryPersonAccountCreatedEvent extends RepULEvent {
    public final DeliveryPersonUniqueIdentifier accountId;
    public final Email email;

    public DeliveryPersonAccountCreatedEvent(DeliveryPersonUniqueIdentifier accountId, Email email) {
        super();
        this.accountId = accountId;
        this.email = email;
    }
}
