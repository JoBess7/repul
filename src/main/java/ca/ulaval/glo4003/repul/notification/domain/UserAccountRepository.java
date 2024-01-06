package ca.ulaval.glo4003.repul.notification.domain;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public interface UserAccountRepository {
    void save(UserAccount account);

    UserAccount getAccountByMealKitId(UniqueIdentifier mealKitId);

    UserAccount getAccountById(UniqueIdentifier accountId);
}
