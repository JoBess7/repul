package ca.ulaval.glo4003.repul.small.delivery.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.Locker;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.DeliveryStatus;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.domain.exception.CargoAlreadyPickedUpException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidCargoIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidDeliveryPersonIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotDeliveredException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotInCargoException;
import ca.ulaval.glo4003.repul.fixture.delivery.DeliverySystemFixture;
import ca.ulaval.glo4003.repul.fixture.delivery.LocationsCatalogFixture;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DeliverySystemTest {
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final MealKitUniqueIdentifier A_MEALKIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final CargoUniqueIdentifier A_CARGO_ID = new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generate();
    private static final String A_LOCATION_NAME = "A_LOCATION_NAME";
    private static final String ANOTHER_LOCATION_NAME = "ANOTHER_LOCATION_NAME";
    private static final MealKitUniqueIdentifier ANOTHER_MEALKIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final DeliveryPersonUniqueIdentifier A_DELIVERY_PERSON_ID = new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final DeliveryPersonUniqueIdentifier ANOTHER_DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final CargoUniqueIdentifier A_INVALID_CARGO_ID = new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier A_INVALID_MEALKIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final Optional<LockerId> A_LOCKER_ID = Optional.of(new LockerId("A_LOCATION_NAME 1", 1));

    @Mock
    private LocationsCatalog mockLocationsCatalog;

    @Test
    public void givenInvalidMealKitId_whenReceivingReadyToBeDeliveredMealKit_shouldThrowInvalidMealKitIdException() {
        DeliverySystem deliverySystem = new DeliverySystemFixture().build();

        assertThrows(InvalidMealKitIdException.class, () -> deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID)));
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldCreateCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));

        assertEquals(1, deliverySystem.getCargos().size());
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldMarkAsReadyToDeliver() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));

        assertEquals(DeliveryStatus.READY_TO_BE_DELIVERED, deliverySystem.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldAssignLockerToMealKit() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));

        assertEquals(A_LOCKER_ID, deliverySystem.getCargos().get(0).getMealKits().get(0).getLockerId());
    }

    @Test
    public void whenGetDeliveryLocation_shouldReturnDeliveryLocations() {
        DeliverySystem deliverySystem = createDeliverySystem();

        List<DeliveryLocation> deliveryLocations = deliverySystem.getDeliveryLocations();

        assertEquals(1, deliveryLocations.size());
        assertEquals(A_DELIVERY_LOCATION_ID, deliveryLocations.get(0).getLocationId());
    }

    @Test
    public void givenValidDeliveryPersonId_whenPickupCargo_shouldSetCargoDeliveryPersonIdToRightValue() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(Optional.of(A_DELIVERY_PERSON_ID), deliverySystem.getCargos().get(0).getDeliveryPersonId());
    }

    @Test
    public void whenPickupCargo_shouldSetDeliveryStatusToInProgress() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(DeliveryStatus.IN_DELIVERY, deliverySystem.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void whenPickupCargo_shouldReturnListOfMealKits() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        List<MealKit> mealKits = deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(A_MEALKIT_ID, mealKits.get(0).getMealKitId());
    }

    @Test
    public void givenInvalidCargoId_whenPickupCargo_shouldThrowInvalidCargoIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        assertThrows(InvalidCargoIdException.class, () -> deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID));
    }

    @Test
    public void givenAlreadyPickedUpCargo_whenPickUpCargo_shouldThrowCargoAlreadyPickedUpException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(CargoAlreadyPickedUpException.class, () -> deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId));
    }

    @Test
    public void whenCancelCargo_shouldSetDeliveryStatusToReadyToDeliver() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        deliverySystem.cancelCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(DeliveryStatus.READY_TO_BE_DELIVERED, deliverySystem.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void whenCancelCargo_shouldUnassignCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.addDeliveryPerson(ANOTHER_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        deliverySystem.cancelCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertDoesNotThrow(() -> deliverySystem.pickupCargo(ANOTHER_DELIVERY_PERSON_ID, cargoId));
    }

    @Test
    public void whenCancelCargo_shouldReturnListOfNotYetDeliveredMealKits() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, ANOTHER_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        List<MealKit> mealKits = deliverySystem.cancelCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(1, mealKits.size());
        assertEquals(ANOTHER_MEALKIT_ID, mealKits.get(0).getMealKitId());
    }

    @Test
    public void givenInvalidCargoId_whenCancelCargo_shouldThrowInvalidCargoIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();

        assertThrows(InvalidCargoIdException.class, () -> deliverySystem.cancelCargo(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID));
    }

    @Test
    public void givenInvalidDeliveryPersonId_whenCancelCargo_shouldThrowInvalidDeliveryPersonIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(InvalidDeliveryPersonIdException.class, () -> deliverySystem.cancelCargo(ANOTHER_DELIVERY_PERSON_ID, cargoId));
    }

    @Test
    public void whenConfirmDelivery_shouldSetDeliveryStatusToDelivered() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertEquals(DeliveryStatus.DELIVERED, deliverySystem.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void givenInvalidCargoId_whenConfirmDelivery_shouldThrowInvalidCargoIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();

        assertThrows(InvalidCargoIdException.class, () -> deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenInvalidDeliveryPersonId_whenConfirmDelivery_shouldThrowInvalidDeliveryPersonIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(InvalidDeliveryPersonIdException.class, () -> deliverySystem.confirmDelivery(ANOTHER_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID));
    }

    @Test
    public void givenValidCargoIdButInvalidMealKitId_whenConfirmDelivery_shouldThrowMealKitNotInCargoException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(MealKitNotInCargoException.class, () -> deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, ANOTHER_MEALKIT_ID));
    }

    @Test
    public void whenRecallDelivery_shouldSetDeliveryStatusToInDelivery() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        deliverySystem.recallDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertEquals(DeliveryStatus.IN_DELIVERY, deliverySystem.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void givenMealKitInDelivery_whenRecallingDelivery_shouldThrowMealKitNotDeliveredException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(MealKitNotDeliveredException.class, () -> deliverySystem.recallDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID));
    }

    @Test
    public void givenInvalidCargoId_whenRecallDelivery_shouldThrowInvalidCargoIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();

        assertThrows(InvalidCargoIdException.class, () -> deliverySystem.recallDelivery(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenInvalidDeliveryPersonId_whenRecallDelivery_shouldThrowInvalidDeliveryPersonIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertThrows(InvalidDeliveryPersonIdException.class, () -> deliverySystem.recallDelivery(ANOTHER_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID));
    }

    @Test
    public void givenMealKitIdNotInCargo_whenRecallDelivery_shouldThrowMealKitNotInCargoException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertThrows(MealKitNotInCargoException.class, () -> deliverySystem.recallDelivery(A_DELIVERY_PERSON_ID, cargoId, ANOTHER_MEALKIT_ID));
    }

    @Test
    public void givenInvalidDeliveryPerson_whenPickupCargo_shouldThrowDeliveryPersonNotFoundException() {
        DeliverySystem deliverySystem = createDeliverySystem();

        assertThrows(DeliveryPersonNotFoundException.class, () -> deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, A_CARGO_ID));
    }

    @Test
    public void whenAddingDeliveryPerson_shouldBeAbleToGet() {
        DeliverySystem deliverySystem = createDeliverySystem();
        DeliveryPersonUniqueIdentifier expectedId = new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();

        deliverySystem.addDeliveryPerson(expectedId);
        List<DeliveryPersonUniqueIdentifier> deliveryPersonIds = deliverySystem.getDeliveryPeople();

        assertEquals(1, deliveryPersonIds.size());
        assertEquals(expectedId, deliveryPersonIds.get(0));
    }

    @Test
    public void givenCargoWithASingleMealKit_whenMovingMealKitFromCargosToPending_shouldRemoveCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEALKIT_ID)));

        deliverySystem.moveMealKitFromCargosToPending(A_MEALKIT_ID);

        assertEquals(0, deliverySystem.getCargos().size());
    }

    @Test
    public void givenCargoWithTwoMealKits_whenMovingMealKitFromCargosToPending_shouldRemoveMealKitFromCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, ANOTHER_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(Arrays.asList(A_MEALKIT_ID, ANOTHER_MEALKIT_ID)));

        deliverySystem.moveMealKitFromCargosToPending(A_MEALKIT_ID);

        assertEquals(1, deliverySystem.getCargos().get(0).getMealKits().size());
    }

    @Test
    public void whenMovingMealKitFromCargosToPending_shouldMakeMealKitReadyToBeDeliveredAgain() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        List<MealKitUniqueIdentifier> newMealKitId = new ArrayList<>(List.of(A_MEALKIT_ID));
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, newMealKitId);

        deliverySystem.moveMealKitFromCargosToPending(A_MEALKIT_ID);

        assertDoesNotThrow(() -> deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, newMealKitId));
    }

    @Test
    public void whenMovingMealKitFromCargosToPending_shouldUnassignLocker() {
        DeliverySystem deliverySystem = createDeliverySystemWithOneLocker();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        List<MealKitUniqueIdentifier> newMealKitId = new ArrayList<>(List.of(A_MEALKIT_ID));
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, newMealKitId);

        deliverySystem.moveMealKitFromCargosToPending(A_MEALKIT_ID);

        assertEquals(1, deliverySystem.getDeliveryLocations().get(0).getRemainingCapacity());
    }

    @Test
    public void givenNoCargos_whenGettingCargosReadyToPickUp_shouldReturnEmptyList() {
        DeliverySystem deliverySystem = createDeliverySystem();

        List<Cargo> cargos = deliverySystem.getCargosReadyToPickUp();

        assertEquals(0, cargos.size());
    }

    @Test
    public void givenOneCargo_whenGettingCargosReadyToPickUpy_shouldReturnListOfOneCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEALKIT_ID)));

        List<Cargo> cargos = deliverySystem.getCargosReadyToPickUp();

        assertEquals(1, cargos.size());
        assertEquals(A_MEALKIT_ID, cargos.get(0).getMealKits().get(0).getMealKitId());
    }

    @Test
    public void givenOnePickedUpCargo_whenGettingCargosReadyToPickUp_shouldReturnEmptyList() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEALKIT_ID)));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        List<Cargo> cargos = deliverySystem.getCargosReadyToPickUp();

        assertEquals(0, cargos.size());
    }

    @Test
    public void whenGettingMealKit_shouldReturnMealKit() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEALKIT_ID)));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();

        MealKit mealKit = deliverySystem.getCargoMealKit(cargoId, A_MEALKIT_ID);

        assertEquals(A_MEALKIT_ID, mealKit.getMealKitId());
    }

    @Test
    public void givenInvalidCargoId_whenGettingMealKit_shouldThrowInvalidCargoIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEALKIT_ID)));

        assertThrows(InvalidCargoIdException.class, () -> deliverySystem.getCargoMealKit(A_INVALID_CARGO_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenInvalidCargoId_whenGettingMealKit_shouldThrowInvalidMealKitIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEALKIT_ID)));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();

        assertThrows(InvalidMealKitIdException.class, () -> deliverySystem.getCargoMealKit(cargoId, A_INVALID_MEALKIT_ID));
    }

    @Test
    public void whenRemovingMealKitFromLocker_shouldDeleteMealKitFromCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, ANOTHER_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        deliverySystem.removeMealKitFromLocker(A_MEALKIT_ID);

        assertThrows(InvalidMealKitIdException.class, () -> deliverySystem.getCargoMealKit(cargoId, A_MEALKIT_ID));
    }

    @Test
    public void givenCargoWithOnlyOneMealKitRemaining_whenRemovingMealKitFromLocker_shouldDeleteCargo() {
        DeliverySystem deliverySystem = createDeliverySystemWithOneLocker();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        deliverySystem.removeMealKitFromLocker(A_MEALKIT_ID);

        assertTrue(deliverySystem.getCargos().isEmpty());
    }

    @Test
    public void whenRemovingMealKitFromLocker_shouldDeleteMeal() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);
        MealKit mealKit = deliverySystem.getCargoMealKit(cargoId, A_MEALKIT_ID);
        Locker locker = mealKit.getDeliveryLocation().findLockerById(mealKit.getLockerId().get());

        assertTrue(locker.isAssigned());

        deliverySystem.removeMealKitFromLocker(A_MEALKIT_ID);

        assertTrue(locker.isUnassigned());
    }

    @Test
    public void givenWaitingMealkit_whenRemovingMealKitFromLocker_shouldAssignLockerToWaitingMealkit() {
        DeliverySystem deliverySystem = createDeliverySystemWithOneLocker();
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.createMealKitInPreparation(A_DELIVERY_LOCATION_ID, ANOTHER_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);
        MealKit mealKit = deliverySystem.getCargoMealKit(cargoId, A_MEALKIT_ID);
        MealKit anotherMealKit = deliverySystem.getCargoMealKit(cargoId, ANOTHER_MEALKIT_ID);
        Locker locker = mealKit.getDeliveryLocation().findLockerById(mealKit.getLockerId().get());

        assertTrue(locker.isAssigned());

        deliverySystem.removeMealKitFromLocker(A_MEALKIT_ID);

        assertTrue(locker.isAssigned());
        assertEquals(locker.getLockerId(), anotherMealKit.getLockerId().get());
    }

    private DeliverySystem createDeliverySystem() {
        return new DeliverySystemFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addDeliveryLocation(new DeliveryLocation(A_DELIVERY_LOCATION_ID, A_LOCATION_NAME, 100))
                .addKitchenLocation(new KitchenLocation(A_KITCHEN_LOCATION_ID, ANOTHER_LOCATION_NAME)).build()).build();
    }

    private DeliverySystem createDeliverySystemWithOneLocker() {
        return new DeliverySystemFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addDeliveryLocation(new DeliveryLocation(A_DELIVERY_LOCATION_ID, A_LOCATION_NAME, 1))
                .addKitchenLocation(new KitchenLocation(A_KITCHEN_LOCATION_ID, ANOTHER_LOCATION_NAME)).build()).build();
    }
}
