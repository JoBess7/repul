package ca.ulaval.glo4003.repul.notification.infrastructure;

import java.util.HashMap;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.notification.application.exception.UserAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.domain.UserAccount;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;

public class InMemoryUserAccountRepository implements UserAccountRepository {
    private final Map<UniqueIdentifier, UserAccount> accounts = new HashMap<>();

    @Override
    public UserAccount getAccountByMealKitId(UniqueIdentifier mealKitId) {
        for (UserAccount account: accounts.values()) {
            if (account.getMealKitIds().stream().anyMatch(mealKit -> mealKit.equals(mealKitId))) {
                return account;
            }
        }
        throw new UserAccountNotFoundException();
    }

    @Override
    public UserAccount getAccountById(UniqueIdentifier accountId) {
        UserAccount userAccount = accounts.get(accountId);
        if (userAccount == null) {
            throw new UserAccountNotFoundException();
        }
        return userAccount;
    }

    @Override
    public void save(UserAccount userAccount) {
        accounts.put(userAccount.getAccountId(), userAccount);
    }
}
