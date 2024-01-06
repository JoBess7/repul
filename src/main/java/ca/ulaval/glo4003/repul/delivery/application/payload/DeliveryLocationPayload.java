package ca.ulaval.glo4003.repul.delivery.application.payload;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;

public record DeliveryLocationPayload(
    DeliveryLocationId deliveryLocationId,
    String name,
    int totalCapacity,
    int remainingCapacity
) {
    public static DeliveryLocationPayload from(DeliveryLocation location) {
        return new DeliveryLocationPayload(location.getLocationId(), location.getName(),
            location.getTotalCapacity(), location.getRemainingCapacity());
    }
}
