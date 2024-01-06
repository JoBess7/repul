package ca.ulaval.glo4003.repul.delivery.application.event;

import java.time.LocalTime;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.exception.LockerNotAssignedException;

public class ConfirmedDeliveryEvent extends RepULEvent {
    public MealKitUniqueIdentifier mealKitId;
    public DeliveryLocationId deliveryLocationId;
    public LockerId lockerId;
    public LocalTime deliveryTime;

    public ConfirmedDeliveryEvent(MealKitUniqueIdentifier mealKitId, DeliveryLocationId deliveryLocationId, Optional<LockerId> lockerId,
                                  LocalTime deliveryTime) {
        super();
        this.mealKitId = mealKitId;
        this.deliveryLocationId = deliveryLocationId;
        this.deliveryTime = deliveryTime;

        if (lockerId.isEmpty()) {
            throw new LockerNotAssignedException();
        }
        this.lockerId = lockerId.get();
    }
}
