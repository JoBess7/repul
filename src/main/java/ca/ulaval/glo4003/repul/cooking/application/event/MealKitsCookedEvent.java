package ca.ulaval.glo4003.repul.cooking.application.event;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;

public class MealKitsCookedEvent extends RepULEvent {
    public final String kitchenLocationId;
    public final List<MealKitDto> mealKits;

    public MealKitsCookedEvent(String kitchenLocationId, List<MealKitDto> mealKits) {
        super();
        this.kitchenLocationId = kitchenLocationId;
        this.mealKits = mealKits;
    }
}
