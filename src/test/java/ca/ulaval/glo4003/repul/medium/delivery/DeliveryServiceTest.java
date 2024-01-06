package ca.ulaval.glo4003.repul.medium.delivery;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargoPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotInDeliveryException;
import ca.ulaval.glo4003.repul.delivery.infrastructure.InMemoryDeliverySystemPersister;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryServiceTest {
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new MealKitUniqueIdentifier(UUID.randomUUID());
    private static final DeliveryPersonUniqueIdentifier A_DELIVERY_PERSON_ACCOUNT_ID = new DeliveryPersonUniqueIdentifier(UUID.randomUUID());
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ACCOUNT_ID = new SubscriberUniqueIdentifier(UUID.randomUUID());
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final LocationsCatalog A_LOCATIONS_CATALOG = new LocationsCatalog(
        List.of(new DeliveryLocation(A_DELIVERY_LOCATION_ID, "vch-1", 10)),
        List.of(new KitchenLocation(A_KITCHEN_LOCATION_ID, "DESJARDINS"))
    );

    private DeliverySystemPersister deliverySystemPersister;
    private RepULEventBus eventBus;
    private DeliveryService deliveryService;

    @BeforeEach
    public void createDeliveryService() {
        deliverySystemPersister = new InMemoryDeliverySystemPersister();
        deliverySystemPersister.save(new DeliverySystem(A_LOCATIONS_CATALOG));
        eventBus = new GuavaEventBus();
        deliveryService = new DeliveryService(deliverySystemPersister, eventBus);
    }

    @Test
    public void givenPendingCargo_whenPickupCargo_shouldRemoveCargoFromAvailableCargos() {
        givenDeliveryPerson(A_DELIVERY_PERSON_ACCOUNT_ID);
        givenConfirmedMealKit(A_MEAL_KIT_ID);
        givenCookedMealKit(A_MEAL_KIT_ID);
        CargoUniqueIdentifier cargoId = deliveryService.getCargosReadyToPickUp().cargoPayloads().get(0).cargoId();

        deliveryService.pickupCargo(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId);

        assertEquals(0, deliveryService.getCargosReadyToPickUp().cargoPayloads().size());
    }

    @Test
    public void givenPickedUpCargo_whenConfirmDelivery_shouldThrowMealKitNotInDeliveryExceptionWhenReconfirming() {
        givenDeliveryPerson(A_DELIVERY_PERSON_ACCOUNT_ID);
        givenConfirmedMealKit(A_MEAL_KIT_ID);
        givenCookedMealKit(A_MEAL_KIT_ID);
        CargoUniqueIdentifier cargoId = deliveryService.getCargosReadyToPickUp().cargoPayloads().get(0).cargoId();
        deliveryService.pickupCargo(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId);

        deliveryService.confirmDelivery(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId, A_MEAL_KIT_ID);

        assertThrows(MealKitNotInDeliveryException.class, () -> deliveryService.confirmDelivery(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId, A_MEAL_KIT_ID));
    }

    @Test
    public void givenPickedUpCargo_whenCancelCargo_shouldMakeCargoAvailableForDelivery() {
        givenDeliveryPerson(A_DELIVERY_PERSON_ACCOUNT_ID);
        givenConfirmedMealKit(A_MEAL_KIT_ID);
        givenCookedMealKit(A_MEAL_KIT_ID);
        CargoUniqueIdentifier cargoId = deliveryService.getCargosReadyToPickUp().cargoPayloads().get(0).cargoId();
        givenPickedUpCargo(cargoId);

        deliveryService.cancelCargo(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId);
        List<CargoPayload> cargosPayloads = deliveryService.getCargosReadyToPickUp().cargoPayloads();

        assertEquals(1, cargosPayloads.size());
    }

    @Test
    public void givenDeliveredMealKit_whenRecallDelivery_shouldBeAbleToReconfirmMealKit() {
        givenDeliveryPerson(A_DELIVERY_PERSON_ACCOUNT_ID);
        givenConfirmedMealKit(A_MEAL_KIT_ID);
        givenCookedMealKit(A_MEAL_KIT_ID);
        CargoUniqueIdentifier cargoId = deliveryService.getCargosReadyToPickUp().cargoPayloads().get(0).cargoId();
        givenPickedUpCargo(cargoId);
        givenDeliveredMealKit(cargoId, A_MEAL_KIT_ID);

        deliveryService.recallDelivery(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId, A_MEAL_KIT_ID);

        assertDoesNotThrow(() -> deliveryService.confirmDelivery(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId, A_MEAL_KIT_ID));
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

    private void givenPickedUpCargo(CargoUniqueIdentifier cargoId) {
        deliveryService.pickupCargo(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId);
    }

    private void givenDeliveredMealKit(CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        deliveryService.confirmDelivery(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId, mealKitId);
    }
}
