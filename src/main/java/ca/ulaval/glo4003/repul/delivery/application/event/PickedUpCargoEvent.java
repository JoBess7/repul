package ca.ulaval.glo4003.repul.delivery.application.event;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public class PickedUpCargoEvent extends RepULEvent {
    public final List<MealKitUniqueIdentifier> mealKitIds;

    public PickedUpCargoEvent(List<MealKitUniqueIdentifier> mealKitIds) {
        super();
        this.mealKitIds = mealKitIds;
    }
}
