package ca.ulaval.glo4003.repul.notification.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class UserAccount extends Account {
    private final Set<MealKitUniqueIdentifier> mealKits;
    public UserAccount(UniqueIdentifier accountId, Email email) {
        super(accountId, email);
        mealKits = new HashSet<>();
    }

    public void addMealKit(MealKitUniqueIdentifier mealKitId) {
        mealKits.add(mealKitId);
    }

    public List<MealKitUniqueIdentifier> getMealKitIds() {
        return mealKits.stream().toList();
    }
}
