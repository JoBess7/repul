package ca.ulaval.glo4003.repul.small.delivery.domain.commons;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeliveryLocationTest {
    private static final DeliveryLocationId A_VALID_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final String A_VALID_LOCATION_NAME = "Vachon";
    private static final int A_VALID_LOCATION_CAPACITY = 10;

    @Test
    public void givenValidValues_whenCreatingDeliveryLocation_shouldCreateDeliveryLocationWithRightValues() {
        DeliveryLocation deliveryLocation = new DeliveryLocation(A_VALID_DELIVERY_LOCATION_ID, A_VALID_LOCATION_NAME, A_VALID_LOCATION_CAPACITY);

        assertEquals(A_VALID_DELIVERY_LOCATION_ID, deliveryLocation.getLocationId());
        assertEquals(A_VALID_LOCATION_NAME, deliveryLocation.getName());
        assertEquals(A_VALID_LOCATION_CAPACITY, deliveryLocation.getTotalCapacity());
        assertEquals(A_VALID_LOCATION_CAPACITY, deliveryLocation.getRemainingCapacity());
    }
}
