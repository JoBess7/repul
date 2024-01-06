package ca.ulaval.glo4003.repul.small.delivery.domain.commons;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KitchenLocationTest {
    private static final KitchenLocationId A_VALID_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final String A_VALID_KITCHEN_LOCATION_NAME = "Vachon";

    @Test
    public void whenGetLocationId_shouldReturnLocationId() {
        KitchenLocation kitchenLocation = new KitchenLocation(A_VALID_KITCHEN_LOCATION_ID, A_VALID_KITCHEN_LOCATION_NAME);

        assertEquals(A_VALID_KITCHEN_LOCATION_ID, kitchenLocation.getLocationId());
    }
}
