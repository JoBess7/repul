package ca.ulaval.glo4003.repul.small.delivery.api.assembler;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.delivery.api.assembler.LocationsCatalogResponseAssembler;
import ca.ulaval.glo4003.repul.delivery.api.response.DeliveryLocationResponse;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationsPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationsCatalogResponseAssemblerTest {
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(DeliveryLocationId.VACHON, "vch-1", 10);
    private static final DeliveryLocation A_SECOND_DELIVERY_LOCATION = new DeliveryLocation(DeliveryLocationId.PEPS, "pep-2", 20);
    private static final DeliveryLocationPayload A_DELIVERY_LOCATION_PAYLOAD =
        new DeliveryLocationPayload(A_DELIVERY_LOCATION.getLocationId(), A_DELIVERY_LOCATION.getName(), A_DELIVERY_LOCATION.getTotalCapacity(),
            A_DELIVERY_LOCATION.getRemainingCapacity());
    private static final DeliveryLocationPayload A_SECOND_DELIVERY_LOCATION_PAYLOAD =
        new DeliveryLocationPayload(A_SECOND_DELIVERY_LOCATION.getLocationId(), A_SECOND_DELIVERY_LOCATION.getName(),
            A_SECOND_DELIVERY_LOCATION.getTotalCapacity(),
            A_SECOND_DELIVERY_LOCATION.getRemainingCapacity());
    private static final DeliveryLocationsPayload A_LOCATIONS_PAYLOAD =
        new DeliveryLocationsPayload(List.of(A_DELIVERY_LOCATION_PAYLOAD, A_SECOND_DELIVERY_LOCATION_PAYLOAD));

    private LocationsCatalogResponseAssembler catalogResponseAssembler;

    @BeforeEach
    public void createAssembler() {
        catalogResponseAssembler = new LocationsCatalogResponseAssembler();
    }

    @Test
    public void givenLocationsPayload_whenToLocationsResponse_shouldReturnListOfLocationResponse() {
        List<DeliveryLocationResponse> deliveryLocationRespons = catalogResponseAssembler.toLocationsResponse(A_LOCATIONS_PAYLOAD);

        assertEquals(A_DELIVERY_LOCATION.getLocationId().toString(), deliveryLocationRespons.get(0).locationId());
        assertEquals(A_DELIVERY_LOCATION.getName(), deliveryLocationRespons.get(0).name());
        assertEquals(A_DELIVERY_LOCATION.getRemainingCapacity(), deliveryLocationRespons.get(0).remainingCapacity());
        assertEquals(A_SECOND_DELIVERY_LOCATION.getLocationId().toString(), deliveryLocationRespons.get(1).locationId());
        assertEquals(A_SECOND_DELIVERY_LOCATION.getName(), deliveryLocationRespons.get(1).name());
        assertEquals(A_SECOND_DELIVERY_LOCATION.getRemainingCapacity(), deliveryLocationRespons.get(1).remainingCapacity());
    }
}
