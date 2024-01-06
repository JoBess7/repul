package ca.ulaval.glo4003.repul.small.delivery.application;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.delivery.application.LocationsCatalogService;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationsPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationsCatalogServiceTest {
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(A_DELIVERY_LOCATION_ID, "Pouliot", 10);
    private static final List<DeliveryLocation> SOME_DELIVERY_LOCATIONS = List.of(A_DELIVERY_LOCATION);
    private LocationsCatalogService locationsCatalogService;
    @Mock
    private DeliverySystemPersister deliverySystemPersister;
    @Mock
    private DeliverySystem mockDeliverySystem;

    @BeforeEach
    public void createLocationsCatalogService() {
        locationsCatalogService = new LocationsCatalogService(deliverySystemPersister);

        when(deliverySystemPersister.get()).thenReturn(mockDeliverySystem);
    }

    @Test
    public void whenGettingPickupLocations_shouldReturnMatchingLocationsPayload() {
        when(mockDeliverySystem.getDeliveryLocations()).thenReturn(SOME_DELIVERY_LOCATIONS);
        DeliveryLocationsPayload expectedDeliveryLocationPayload = new DeliveryLocationsPayload(List.of(
            new DeliveryLocationPayload(A_DELIVERY_LOCATION.getLocationId(), A_DELIVERY_LOCATION.getName(), A_DELIVERY_LOCATION.getTotalCapacity(),
                A_DELIVERY_LOCATION.getRemainingCapacity())));

        DeliveryLocationsPayload deliveryLocationsPayload = locationsCatalogService.getDeliveryLocations();

        assertEquals(expectedDeliveryLocationPayload, deliveryLocationsPayload);
    }
}
