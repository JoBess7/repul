package ca.ulaval.glo4003.repul.small.delivery.application.payload;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeliveryLocationPayloadTest {
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final String LOCATION_NAME = "a name";
    private static final int LOCATION_TOTAL_CAPACITY = 10;
    private static final int LOCATION_REMAINING_CAPACITY = LOCATION_TOTAL_CAPACITY;

    @Test
    public void givenDeliveryLocation_whenUsingFrom_shouldReturnCorrectDeliveryLocationPayload() {
        DeliveryLocationPayload expectedDeliveryLocationPayload =
            new DeliveryLocationPayload(A_DELIVERY_LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY, LOCATION_REMAINING_CAPACITY);
        DeliveryLocation location = new DeliveryLocation(A_DELIVERY_LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY);

        DeliveryLocationPayload actualDeliveryLocationPayload = DeliveryLocationPayload.from(location);

        assertEquals(expectedDeliveryLocationPayload, actualDeliveryLocationPayload);
    }
}
