package ca.ulaval.glo4003.repul.delivery.application.event;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public class CanceledCargoEvent extends RepULEvent {
    public final List<MealKitUniqueIdentifier> mealKitIds;

    public CanceledCargoEvent(List<MealKitUniqueIdentifier> mealKitIds) {
        this.mealKitIds = mealKitIds;
    }
}
