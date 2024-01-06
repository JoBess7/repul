package ca.ulaval.glo4003.repul.notification.domain;

import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;

public interface DeliveryPersonAccountRepository {
    void save(DeliveryPersonAccount account);

    DeliveryPersonAccount getByAccountId(DeliveryPersonUniqueIdentifier accountId);
}
