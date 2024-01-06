package ca.ulaval.glo4003.repul.small.notification.application;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.notification.application.MessageFactory;
import ca.ulaval.glo4003.repul.notification.domain.NotificationMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MessageFactoryTest {
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final CargoUniqueIdentifier A_CARGO_ID = new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generate();
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final LocalTime A_DELIVERY_TIME = LocalTime.now();
    private static final LockerId A_LOCKER_ID = new LockerId("locker", 5);
    private static final List<MealKitDto> A_MEAL_KIT_DTO = List.of(new MealKitDto(A_DELIVERY_LOCATION_ID, Optional.of(A_LOCKER_ID), A_MEAL_KIT_ID));
    private static final String EXPECTED_READY_TO_BE_DELIVERED_MESSAGE =
        "Your meal kits (cargo id: " + A_CARGO_ID.getUUID() + ") are ready to be fetched from " + A_KITCHEN_LOCATION_ID + ".\n" +
            "Here is the list of meal kits to be delivered:\n" + "MealKit ID " + A_MEAL_KIT_DTO.get(0).mealKitId().getUUID() + " to " +
            A_MEAL_KIT_DTO.get(0).deliveryLocationId().toString() + " in box 5\n";
    private static final String EXPECTED_DELIVERY_MESSAGE =
        "Your meal kit with id " + A_MEAL_KIT_ID.getUUID() + " is ready to be fetched from " + A_DELIVERY_LOCATION_ID + " in the locker " +
            A_LOCKER_ID.lockerNumber() + ".\n" + "It arrived at " + A_DELIVERY_TIME + ".\n" + "Come get it!";
    private static final String EXPECTED_READY_FOR_KITCHEN_PICKUP_MESSAGE = "Your meal kit with id " + A_MEAL_KIT_ID.getUUID() +
        " is ready to be fetched from the " + A_KITCHEN_LOCATION_ID + " kitchen.\n" + "Come get it!";
    private MessageFactory messageFactory;

    @BeforeEach
    public void createMessageFactory() {
        messageFactory = new MessageFactory();
    }

    @Test
    public void whenCreatingDeliveryMessage_shouldReturnMessage() {
        NotificationMessage message = messageFactory.createDeliveredMessage(A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID, A_DELIVERY_TIME, A_LOCKER_ID);

        assertEquals(EXPECTED_DELIVERY_MESSAGE, message.body());
        assertFalse(message.title().isEmpty());
    }

    @Test
    public void whenCreatingReadyToBeDeliveredMessage_shouldReturnMessage() {
        NotificationMessage message = messageFactory.createReadyToBeDeliveredMessage(A_CARGO_ID, A_KITCHEN_LOCATION_ID, A_MEAL_KIT_DTO);

        assertEquals(EXPECTED_READY_TO_BE_DELIVERED_MESSAGE, message.body());
        assertFalse(message.title().isEmpty());
    }

    @Test
    public void whenCreatingMealKitAvailableForPickUpMessage_shouldReturnMessage() {
        NotificationMessage message = messageFactory.createMealKitAvailableForPickUpMessage(A_MEAL_KIT_ID, A_KITCHEN_LOCATION_ID);

        assertEquals(EXPECTED_READY_FOR_KITCHEN_PICKUP_MESSAGE, message.body());
        assertFalse(message.title().isEmpty());
    }
}
