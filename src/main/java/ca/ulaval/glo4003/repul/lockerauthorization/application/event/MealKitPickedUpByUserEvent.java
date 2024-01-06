package ca.ulaval.glo4003.repul.lockerauthorization.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public class MealKitPickedUpByUserEvent extends RepULEvent {
    public final MealKitUniqueIdentifier mealKitId;

    public MealKitPickedUpByUserEvent(MealKitUniqueIdentifier mealKitId) {
        super();
        this.mealKitId = mealKitId;
    }
}
