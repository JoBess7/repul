package ca.ulaval.glo4003.repul.notification.domain;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;

public class DeliveryPersonAccount extends Account {
    public DeliveryPersonAccount(DeliveryPersonUniqueIdentifier accountId, Email email) {
        super(accountId, email);
    }
}
