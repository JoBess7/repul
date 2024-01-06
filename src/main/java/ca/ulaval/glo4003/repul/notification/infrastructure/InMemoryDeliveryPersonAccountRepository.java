package ca.ulaval.glo4003.repul.notification.infrastructure;

import java.util.HashMap;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.notification.application.exception.DeliveryPersonAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;

public class InMemoryDeliveryPersonAccountRepository implements DeliveryPersonAccountRepository {
    private final Map<UniqueIdentifier, DeliveryPersonAccount> accounts = new HashMap<>();

    @Override
    public void save(DeliveryPersonAccount account) {
        accounts.put(account.getAccountId(), account);
    }

    @Override
    public DeliveryPersonAccount getByAccountId(DeliveryPersonUniqueIdentifier accountId) {
        DeliveryPersonAccount deliveryPersonAccount = accounts.get(accountId);
        if (deliveryPersonAccount == null) {
            throw new DeliveryPersonAccountNotFoundException();
        }
        return deliveryPersonAccount;
    }
}
