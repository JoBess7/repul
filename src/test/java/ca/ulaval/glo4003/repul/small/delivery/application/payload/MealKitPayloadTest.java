package ca.ulaval.glo4003.repul.small.delivery.application.payload;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.payload.MealKitPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.LockerAssignator;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.DeliveryStatus;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MealKitPayloadTest {

    private static final DeliveryLocationId DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final String LOCATION_NAME = "VACHON";
    private static final int LOCATION_TOTAL_CAPACITY = 10;
    private static final DeliveryLocation DELIVERY_LOCATION = new DeliveryLocation(DELIVERY_LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY);
    private static final String MEAL_KIT_ID = UUID.randomUUID().toString();
    private static final DeliveryStatus DELIVERY_STATUS = DeliveryStatus.READY_TO_BE_DELIVERED;
    private static final MealKitUniqueIdentifier A_MEAL_KIT_UNIQUE_IDENTIFIER =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(MEAL_KIT_ID);
    private static final String NOT_ASSIGN_LOCKER_NUMBER = "Not assigned at this moment";
    private static final String LOCKER_NUMBER = "1";
    private final LockerAssignator lockerAssignator = new LockerAssignator(List.of(DELIVERY_LOCATION));

    @Test
    public void givenMealKitWithNoLocker_whenUsingFrom_shouldReturnCorrectMealKitPayload() {
        MealKit mealKit = new MealKit(DELIVERY_LOCATION, A_MEAL_KIT_UNIQUE_IDENTIFIER, DELIVERY_STATUS);
        MealKitPayload expectedMealKitPayload = new MealKitPayload(A_MEAL_KIT_UNIQUE_IDENTIFIER, DELIVERY_LOCATION_ID.toString(), NOT_ASSIGN_LOCKER_NUMBER);

        MealKitPayload actualMealKitPayload = MealKitPayload.from(mealKit);

        assertEquals(expectedMealKitPayload, actualMealKitPayload);
    }

    @Test
    public void givenMealKitWithAssignedLocker_whenUsingFrom_shouldReturnCorrectMealKitPayload() {
        MealKit mealKit = new MealKit(DELIVERY_LOCATION, A_MEAL_KIT_UNIQUE_IDENTIFIER, DELIVERY_STATUS);
        lockerAssignator.assignLocker(mealKit);
        MealKitPayload expectedMealKitPayload = new MealKitPayload(A_MEAL_KIT_UNIQUE_IDENTIFIER, DELIVERY_LOCATION_ID.toString(), LOCKER_NUMBER);

        MealKitPayload actualMealKitPayload = MealKitPayload.from(mealKit);

        assertEquals(expectedMealKitPayload, actualMealKitPayload);
    }
}
