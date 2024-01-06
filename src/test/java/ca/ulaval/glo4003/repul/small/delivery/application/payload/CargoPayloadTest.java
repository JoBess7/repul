package ca.ulaval.glo4003.repul.small.delivery.application.payload;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargoPayload;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CargoPayloadTest {

    private static final String CARGO_ID = UUID.randomUUID().toString();
    private static final CargoUniqueIdentifier CARGO_UNIQUE_IDENTIFIER = new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generateFrom(CARGO_ID);
    private static final KitchenLocationId KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final String KITCHEN_LOCATION_NAME = "VACHON";
    private static final KitchenLocation KITCHEN_LOCATION = new KitchenLocation(KITCHEN_LOCATION_ID, KITCHEN_LOCATION_NAME);
    private static final Cargo CARGO = new Cargo(CARGO_UNIQUE_IDENTIFIER, KITCHEN_LOCATION, Collections.emptyList());

    @Test
    public void whenUsingFrom_shouldReturnCorrectCargoPayload() {
        CargoPayload expectedCargoPayload = new CargoPayload(CARGO_UNIQUE_IDENTIFIER, KITCHEN_LOCATION_ID.toString(), Collections.emptyList());

        CargoPayload actualCargoPayload = CargoPayload.from(CARGO);

        assertEquals(expectedCargoPayload, actualCargoPayload);
    }
}
