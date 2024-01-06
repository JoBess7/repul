package ca.ulaval.glo4003.repul.delivery.application;

import java.time.LocalTime;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.event.CanceledCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargosPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.delivery.domain.exception.LockerNotAssignedException;

public class DeliveryService {
    private final DeliverySystemPersister deliverySystemPersister;
    private final RepULEventBus eventBus;

    public DeliveryService(DeliverySystemPersister deliverySystemPersister, RepULEventBus eventBus) {
        this.deliverySystemPersister = deliverySystemPersister;
        this.eventBus = eventBus;
    }

    public void createDeliveryPersonAccount(DeliveryPersonUniqueIdentifier accountId) {
        DeliverySystem deliverySystem = deliverySystemPersister.get();

        deliverySystem.addDeliveryPerson(accountId);

        deliverySystemPersister.save(deliverySystem);
    }

    public void createMealKitInPreparation(DeliveryLocationId deliveryLocationId, MealKitUniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemPersister.get();

        deliverySystem.createMealKitInPreparation(deliveryLocationId, mealKitId);

        deliverySystemPersister.save(deliverySystem);
    }

    public void createCargo(KitchenLocationId kitchenLocationId, List<MealKitUniqueIdentifier> mealKitIds) {
        DeliverySystem deliverySystem = deliverySystemPersister.get();

        Cargo cargo = deliverySystem.receiveReadyToBeDeliveredMealKits(kitchenLocationId, mealKitIds);

        deliverySystemPersister.save(deliverySystem);

        sendMealKitReceivedForDeliveryEvent(cargo, deliverySystem);
    }

    public void recallMealKit(MealKitUniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemPersister.get();

        deliverySystem.moveMealKitFromCargosToPending(mealKitId);

        deliverySystemPersister.save(deliverySystem);
    }

    public void removeMealKitFromLocker(MealKitUniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemPersister.get();

        deliverySystem.removeMealKitFromLocker(mealKitId);

        deliverySystemPersister.save(deliverySystem);
    }

    private void sendMealKitReceivedForDeliveryEvent(Cargo cargo, DeliverySystem deliverySystem) {
        List<MealKitDto> mealKits = cargo.getMealKits().stream().map(
            mealKit -> new MealKitDto(mealKit.getDeliveryLocation().getLocationId(), mealKit.getLockerId(), mealKit.getMealKitId())
        ).toList();

        MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent = new MealKitReceivedForDeliveryEvent(
            cargo.getCargoId(), cargo.getKitchenLocation().getLocationId(), deliverySystem.getDeliveryPeople(), mealKits);
        eventBus.publish(mealKitReceivedForDeliveryEvent);
    }

    public CargosPayload getCargosReadyToPickUp() {
        DeliverySystem deliverySystem = deliverySystemPersister.get();

        return CargosPayload.from(deliverySystem.getCargosReadyToPickUp());
    }

    public void pickupCargo(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId) {
        DeliverySystem deliverySystem = deliverySystemPersister.get();

        List<MealKit> mealKits = deliverySystem.pickupCargo(deliveryPersonId, cargoId);

        deliverySystemPersister.save(deliverySystem);

        sendPickedUpCargoEvent(mealKits);
    }

    private void sendPickedUpCargoEvent(List<MealKit> mealKits) {
        List<MealKitUniqueIdentifier> mealKitIds = mealKits.stream().map(MealKit::getMealKitId).toList();

        eventBus.publish(new PickedUpCargoEvent(mealKitIds));
    }

    public void cancelCargo(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId) {
        DeliverySystem deliverySystem = deliverySystemPersister.get();

        List<MealKit> mealKits = deliverySystem.cancelCargo(deliveryPersonId, cargoId);

        deliverySystemPersister.save(deliverySystem);

        List<MealKitUniqueIdentifier> mealKitIds = mealKits.stream().map(MealKit::getMealKitId).toList();
        eventBus.publish(new CanceledCargoEvent(mealKitIds));
    }

    public void confirmDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemPersister.get();

        deliverySystem.confirmDelivery(deliveryPersonId, cargoId, mealKitId);

        MealKit mealKit = deliverySystem.getCargoMealKit(cargoId, mealKitId);

        deliverySystemPersister.save(deliverySystem);

        ConfirmedDeliveryEvent event = new ConfirmedDeliveryEvent(mealKit.getMealKitId(), mealKit.getDeliveryLocation().getLocationId(),
            mealKit.getLockerId(), LocalTime.now());
        eventBus.publish(event);
    }

    public LockerId recallDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        DeliverySystem deliverySystem = deliverySystemPersister.get();

        MealKit mealKit = deliverySystem.recallDelivery(deliveryPersonId, cargoId, mealKitId);
        LockerId lockerId = mealKit.getLockerId().orElseThrow(LockerNotAssignedException::new);

        deliverySystemPersister.save(deliverySystem);

        DeliveryLocationId deliveryLocationId = mealKit.getDeliveryLocation().getLocationId();
        eventBus.publish(new RecalledDeliveryEvent(mealKitId, lockerId, deliveryLocationId));

        return lockerId;
    }
}
