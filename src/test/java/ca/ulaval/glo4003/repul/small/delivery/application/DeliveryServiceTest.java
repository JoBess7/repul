package ca.ulaval.glo4003.repul.small.delivery.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.delivery.application.event.CanceledCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.DeliveryStatus;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.fixture.delivery.MealKitFixture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {
    private static final String A_DELIVERY_PERSON_ID = UUID.randomUUID().toString();
    private static final String A_CARGO_ID = UUID.randomUUID().toString();
    private static final String A_MEAL_KIT_ID = UUID.randomUUID().toString();
    private static final CargoUniqueIdentifier A_CARGO_UNIQUE_IDENTIFIER =
        new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generateFrom(A_CARGO_ID);
    private static final MealKitUniqueIdentifier A_MEAL_KIT_UNIQUE_IDENTIFIER =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(A_MEAL_KIT_ID);
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final KitchenLocation A_KITCHEN_LOCATION = new KitchenLocation(A_KITCHEN_LOCATION_ID, "Vachon");
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(A_DELIVERY_LOCATION_ID, "Pouliot", 10);
    private static final MealKit A_MEAL_KIT = new MealKit(A_DELIVERY_LOCATION, A_MEAL_KIT_UNIQUE_IDENTIFIER, DeliveryStatus.PENDING);
    private static final Cargo A_CARGO = new Cargo(A_CARGO_UNIQUE_IDENTIFIER, A_KITCHEN_LOCATION, new ArrayList<>(List.of(A_MEAL_KIT)));
    private static final MealKit
        A_DELIVERED_MEAL_KIT = new MealKit(A_DELIVERY_LOCATION, A_MEAL_KIT_UNIQUE_IDENTIFIER, DeliveryStatus.DELIVERED);
    private static final Optional<LockerId> A_LOCKER_ID = Optional.of(new LockerId("some id", 1));
    private static final MealKit A_MEAL_KIT_WITH_LOCKER = new MealKitFixture().withLockerId(A_LOCKER_ID).build();
    private static final DeliveryPersonUniqueIdentifier A_DELIVERY_PERSON_UNIQUE_IDENTIFIER =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom(A_DELIVERY_PERSON_ID);

    private DeliveryService deliveryService;
    @Mock
    private DeliverySystemPersister mockDeliverySystemPersister;
    @Mock
    private DeliverySystem mockDeliverySystem;
    @Mock
    private RepULEventBus mockRepULEventBus;

    @BeforeEach
    public void createDeliveryService() {
        deliveryService = new DeliveryService(mockDeliverySystemPersister, mockRepULEventBus);

        when(mockDeliverySystemPersister.get()).thenReturn(mockDeliverySystem);
    }

    @Test
    public void whenPickupCargo_shouldPublishPickedUpCargoEvent() {
        deliveryService.pickupCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);

        verify(mockRepULEventBus).publish(any(PickedUpCargoEvent.class));
    }

    @Test
    public void whenCancelCargo_shouldPublishCanceledCargoEvent() {
        deliveryService.cancelCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);

        verify(mockRepULEventBus).publish(any(CanceledCargoEvent.class));
    }

    @Test
    public void whenCreatingCargo_shouldPublishMealKitReadyToDeliverEvent() {
        when(mockDeliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER))).thenReturn(A_CARGO);

        deliveryService.createCargo(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER));

        verify(mockRepULEventBus).publish(any(MealKitReceivedForDeliveryEvent.class));
    }

    @Test
    public void whenConfirmDelivery_shouldPublishConfirmedDeliveryEvent() {
        when(mockDeliverySystem.getCargoMealKit(any(), any())).thenReturn(A_MEAL_KIT_WITH_LOCKER);

        deliveryService.confirmDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(mockRepULEventBus).publish(any(ConfirmedDeliveryEvent.class));
    }

    @Test
    public void wheRecallDelivery_shouldPublishRecalledDeliveryEvent() {
        when(mockDeliverySystem.recallDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER)).thenReturn(
            A_DELIVERED_MEAL_KIT);
        A_DELIVERED_MEAL_KIT.assignLocker(A_LOCKER_ID);

        deliveryService.recallDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(mockRepULEventBus).publish(any(RecalledDeliveryEvent.class));
    }
}
