package ca.ulaval.glo4003.repul.delivery.api.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.delivery.api.response.DeliveryLocationResponse;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationsPayload;

public class LocationsCatalogResponseAssembler {
    public List<DeliveryLocationResponse> toLocationsResponse(DeliveryLocationsPayload deliveryLocationsPayload) {
        return deliveryLocationsPayload.deliveryLocations().stream().map(this::getDeliveryLocationResponse).toList();
    }

    private DeliveryLocationResponse getDeliveryLocationResponse(DeliveryLocationPayload deliveryLocation) {
        return new DeliveryLocationResponse(
            deliveryLocation.deliveryLocationId().toString(),
            deliveryLocation.name(),
            deliveryLocation.remainingCapacity()
        );
    }
}
