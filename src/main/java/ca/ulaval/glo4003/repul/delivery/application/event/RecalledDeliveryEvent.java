package ca.ulaval.glo4003.repul.delivery.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;

public class RecalledDeliveryEvent extends RepULEvent {
    public final MealKitUniqueIdentifier mealKitId;
    public final LockerId lockerId;
    public final DeliveryLocationId deliveryLocationId;

    public RecalledDeliveryEvent(MealKitUniqueIdentifier mealKitId, LockerId lockerId, DeliveryLocationId deliveryLocationId) {
        this.mealKitId = mealKitId;
        this.lockerId = lockerId;
        this.deliveryLocationId = deliveryLocationId;
    }
}
