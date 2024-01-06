package ca.ulaval.glo4003.repul.medium.delivery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.api.DeliveryEventHandler;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.delivery.infrastructure.InMemoryDeliverySystemPersister;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import com.google.common.eventbus.Subscribe;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeliveryEventHandlerTest {
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final DeliveryPersonUniqueIdentifier A_DELIVERY_PERSON_UNIQUE_IDENTIFIER =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier ANOTHER_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final MealKitDto A_MEALKIT_DTO = new MealKitDto(A_MEAL_KIT_ID, true);
    private static final MealKitDto ANOTHER_MEALKIT_DTO = new MealKitDto(ANOTHER_MEAL_KIT_ID, true);
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final Email AN_EMAIL = new Email("courriel@ulaval.ca");
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final LocalDate A_DATE = LocalDate.now();
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;

    private DeliveryEventHandler deliveryEventHandler;
    private DeliveryService deliveryService;
    private RepULEventBus eventBus;
    private DeliverySystemPersister deliverySystemPersister;

    @BeforeEach
    public void createDeliveryEventHandler() {
        eventBus = new GuavaEventBus();
        deliverySystemPersister = new InMemoryDeliverySystemPersister();
        deliverySystemPersister.save(createDeliverySystem());
        deliveryService = new DeliveryService(deliverySystemPersister, eventBus);
        deliveryEventHandler = new DeliveryEventHandler(deliveryService);
        eventBus.register(deliveryEventHandler);
    }
    @Test
    public void whenHandlingDeliveryPersonAccountCreatedEvent_shouldAddDeliveryPersonAccountToDeliverySystem() {
        DeliveryPersonAccountCreatedEvent event = new DeliveryPersonAccountCreatedEvent(
            A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, AN_EMAIL);

        eventBus.publish(event);

        assertEquals(1, deliverySystemPersister.get().getDeliveryPeople().size());
    }

    @Test
    public void givenConfirmedMealKits_whenHandlingMealKitsCookedEvent_shouldCreateCargo() {
        givenConfirmedMealKit(A_MEAL_KIT_ID);
        givenConfirmedMealKit(ANOTHER_MEAL_KIT_ID);
        MealKitsCookedEvent mealKitsCookedEvent = new MealKitsCookedEvent(A_KITCHEN_LOCATION_ID.toString(), List.of(A_MEALKIT_DTO, ANOTHER_MEALKIT_DTO));

        eventBus.publish(mealKitsCookedEvent);

        assertEquals(1, deliveryService.getCargosReadyToPickUp().cargoPayloads().size());
        assertEquals(2, deliveryService.getCargosReadyToPickUp().cargoPayloads().get(0).mealKitsPayload().size());
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldAddMealKitToDeliverySystem() {
        MealKitConfirmedEvent mealKitConfirmedEvent = new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID,
            A_MEAL_KIT_TYPE, Optional.of(A_DELIVERY_LOCATION_ID), A_DATE);

        eventBus.publish(mealKitConfirmedEvent);

        assertDoesNotThrow(() -> deliveryService.createCargo(A_KITCHEN_LOCATION_ID,
            List.of(A_MEAL_KIT_ID)));
    }

    @Test
    public void givenMealKitInCargo_whenHandlingRecallCookedMealKitEvent_shouldRemoveMealKitFromCargo() {
        givenDeliveryPerson(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER);
        givenConfirmedMealKit(A_MEAL_KIT_ID);
        givenCookedMealKit(A_MEAL_KIT_ID);
        RecallCookedMealKitEvent recallCookedMealKitEvent = new RecallCookedMealKitEvent(A_MEAL_KIT_ID);

        eventBus.publish(recallCookedMealKitEvent);

        assertEquals(0, deliveryService.getCargosReadyToPickUp().cargoPayloads().size());
    }

    @Test
    public void givenMealKitInCargo_whenHandlingMealKitPickedUpByUserEvent_shouldRemoveMealKitFromCargo() {
        MealKitReceivedForDeliveryEventListener listener = createMealKitReceivedForDeliveryEventListener();
        givenDeliveryPerson(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER);
        givenConfirmedMealKit(A_MEAL_KIT_ID);
        givenCookedMealKit(A_MEAL_KIT_ID);
        givenPickUpCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, listener.cargoId);
        assertNotNull(listener.cargoId);
        givenDeliveredMealKit(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, listener.cargoId, A_MEAL_KIT_ID);
        MealKitPickedUpByUserEvent mealKitPickedUpByUserEvent = new MealKitPickedUpByUserEvent(A_MEAL_KIT_ID);

        eventBus.publish(mealKitPickedUpByUserEvent);

        assertThrows(InvalidMealKitIdException.class, () -> deliveryService.removeMealKitFromLocker(A_MEAL_KIT_ID));
    }

    private DeliverySystem createDeliverySystem() {
        DeliveryLocation deliveryLocation = new DeliveryLocation(A_DELIVERY_LOCATION_ID,
            "A_NAME", 10);
        KitchenLocation kitchenLocation = new KitchenLocation(A_KITCHEN_LOCATION_ID, "Kitchen");
        LocationsCatalog locationsCatalog = new LocationsCatalog(List.of(deliveryLocation), List.of(kitchenLocation));
        return new DeliverySystem(locationsCatalog);
    }

    private void givenConfirmedMealKit(MealKitUniqueIdentifier mealKitId) {
        deliveryService.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, mealKitId);
    }

    private void givenCookedMealKit(MealKitUniqueIdentifier mealKitId) {
        deliveryService.createCargo(A_KITCHEN_LOCATION_ID, List.of(mealKitId));
    }

    private void givenDeliveryPerson(DeliveryPersonUniqueIdentifier accountId) {
        deliveryService.createDeliveryPersonAccount(accountId);
    }

    private void givenPickUpCargo(DeliveryPersonUniqueIdentifier accountId, CargoUniqueIdentifier cargoId) {
        deliveryService.pickupCargo(accountId, cargoId);
    }

    private void givenDeliveredMealKit(DeliveryPersonUniqueIdentifier accountId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        deliveryService.confirmDelivery(accountId, cargoId, mealKitId);
    }

    private MealKitReceivedForDeliveryEventListener createMealKitReceivedForDeliveryEventListener() {
        MealKitReceivedForDeliveryEventListener listener = new MealKitReceivedForDeliveryEventListener();
        eventBus.register(listener);
        return listener;
    }

    private class MealKitReceivedForDeliveryEventListener {
        public CargoUniqueIdentifier cargoId;

        @Subscribe
        public void getCargoId(MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent) {
            cargoId = mealKitReceivedForDeliveryEvent.cargoId;
        }
    }
}
