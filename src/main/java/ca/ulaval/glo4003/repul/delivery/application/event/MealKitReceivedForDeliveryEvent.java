package ca.ulaval.glo4003.repul.delivery.application.event;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;

public class MealKitReceivedForDeliveryEvent extends RepULEvent {
    public final KitchenLocationId kitchenLocationId;
    public final CargoUniqueIdentifier cargoId;
    public final List<DeliveryPersonUniqueIdentifier> availableDeliveryPeople;
    public final List<MealKitDto> mealKitDtos;

    public MealKitReceivedForDeliveryEvent(CargoUniqueIdentifier cargoId, KitchenLocationId kitchenLocationId,
                                           List<DeliveryPersonUniqueIdentifier> availableDeliveryPeople, List<MealKitDto> mealKitDtos) {
        super();
        this.cargoId = cargoId;
        this.availableDeliveryPeople = availableDeliveryPeople;
        this.kitchenLocationId = kitchenLocationId;
        this.mealKitDtos = mealKitDtos;
    }
}
