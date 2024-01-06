package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.exception.LockerNotAssignedException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotDeliveredException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotInDeliveryException;

public class MealKit {
    private final DeliveryLocation deliveryLocation;
    private final MealKitUniqueIdentifier mealKitId;
    private Optional<LockerId> lockerId;
    private DeliveryStatus status;

    public MealKit(DeliveryLocation deliveryLocation, MealKitUniqueIdentifier mealKitId, DeliveryStatus status) {
        this.deliveryLocation = deliveryLocation;
        this.mealKitId = mealKitId;
        this.status = status;
        this.lockerId = Optional.empty();
    }

    public MealKitUniqueIdentifier getMealKitId() {
        return mealKitId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public Optional<LockerId> getLockerId() {
        return lockerId;
    }

    public DeliveryLocation getDeliveryLocation() {
        return deliveryLocation;
    }

    public void assignLocker(Optional<LockerId> lockerId) {
        this.lockerId = lockerId;
    }

    public void markAsReadyToBeDelivered() {
        status = DeliveryStatus.READY_TO_BE_DELIVERED;
    }

    public void pickupReadyToDeliverMealKit() {
        status = DeliveryStatus.IN_DELIVERY;
    }

    public void cancelDelivery() {
        if (status != DeliveryStatus.IN_DELIVERY) {
            throw new MealKitNotInDeliveryException();
        }
        status = DeliveryStatus.READY_TO_BE_DELIVERED;
    }

    public void confirmDelivery() {
        if (status != DeliveryStatus.IN_DELIVERY) {
            throw new MealKitNotInDeliveryException();
        }
        if (lockerId.isEmpty()) {
            throw new LockerNotAssignedException();
        }
        status = DeliveryStatus.DELIVERED;
    }

    public void recallDelivery() {
        if (status != DeliveryStatus.DELIVERED) {
            throw new MealKitNotDeliveredException();
        }
        status = DeliveryStatus.IN_DELIVERY;
    }

    public boolean isNotAlreadyDelivered() {
        return status == DeliveryStatus.READY_TO_BE_DELIVERED || status == DeliveryStatus.PENDING || status == DeliveryStatus.IN_DELIVERY;
    }
}
