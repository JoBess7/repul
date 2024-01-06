package ca.ulaval.glo4003.repul.lockerauthorization.domain;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;

public class Order {
    private final SubscriberUniqueIdentifier userId;
    private final MealKitUniqueIdentifier mealKitId;
    private Optional<LockerId> lockerId;

    public Order(SubscriberUniqueIdentifier userId, MealKitUniqueIdentifier mealKitId) {
        this.userId = userId;
        this.mealKitId = mealKitId;
        this.lockerId = Optional.empty();
    }

    public void assignLocker(LockerId lockerId) {
        this.lockerId = Optional.of(lockerId);
    }

    public void unassignLocker() {
        this.lockerId = Optional.empty();
    }

    public SubscriberUniqueIdentifier getUserId() {
        return userId;
    }

    public MealKitUniqueIdentifier getMealKitId() {
        return mealKitId;
    }

    public Optional<LockerId> getLockerId() {
        return lockerId;
    }

    public boolean hasLockerId(LockerId lockerIdToCompare) {
        return lockerId.isPresent() && lockerId.get().equals(lockerIdToCompare);
    }
}
