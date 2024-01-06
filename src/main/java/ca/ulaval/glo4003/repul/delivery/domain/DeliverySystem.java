package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKitFactory;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidCargoIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;

public class DeliverySystem {
    private final Map<MealKitUniqueIdentifier, MealKit> pendingMealKits = new HashMap<>();
    private final List<Cargo> cargos = new ArrayList<>();
    private final LocationsCatalog locationsCatalog;
    private final CargoFactory cargoFactory;
    private final MealKitFactory mealKitFactory;
    private final List<DeliveryPersonUniqueIdentifier> deliveryPersonIds = new ArrayList<>();
    private final LockerAssignator lockerAssignator;

    public DeliverySystem(LocationsCatalog locationsCatalog) {
        this.locationsCatalog = locationsCatalog;
        this.cargoFactory = new CargoFactory();
        this.mealKitFactory = new MealKitFactory();
        this.lockerAssignator = new LockerAssignator(locationsCatalog.getDeliveryLocations());
    }

    public void createMealKitInPreparation(DeliveryLocationId deliveryLocationId, MealKitUniqueIdentifier mealKitId) {
        MealKit createdMealKit = mealKitFactory.createMealKit(locationsCatalog.getDeliveryLocation(deliveryLocationId), mealKitId);
        pendingMealKits.put(mealKitId, createdMealKit);
    }

    public List<Cargo> getCargosReadyToPickUp() {
        return cargos.stream().filter(Cargo::isReadyToBeDelivered).toList();
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public Cargo receiveReadyToBeDeliveredMealKits(KitchenLocationId kitchenLocationId, List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(mealKitId -> {
            if (!pendingMealKits.containsKey(mealKitId)) {
                throw new InvalidMealKitIdException();
            }
        });

        List<MealKit> mealKits = mealKitIds.stream().map(this.pendingMealKits::remove).collect(Collectors.toCollection(ArrayList::new));

        mealKits.forEach(lockerAssignator::assignLocker);

        mealKits.forEach(MealKit::markAsReadyToBeDelivered);

        Cargo cargo = cargoFactory.createCargo(locationsCatalog.getKitchenLocation(kitchenLocationId), mealKits);

        cargos.add(cargo);

        return cargo;
    }

    public List<DeliveryLocation> getDeliveryLocations() {
        return locationsCatalog.getDeliveryLocations();
    }

    public List<MealKit> pickupCargo(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId) {
        validateDeliveryPersonExists(deliveryPersonId);

        Cargo cargoToPickUp = getCargo(cargoId);

        return cargoToPickUp.pickupCargo(deliveryPersonId);
    }

    private void validateDeliveryPersonExists(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        if (!deliveryPersonIds.contains(deliveryPersonId)) {
            throw new DeliveryPersonNotFoundException();
        }
    }

    public List<MealKit> cancelCargo(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId) {
        return getCargo(cargoId).cancelCargo(deliveryPersonId);
    }

    public void confirmDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        getCargo(cargoId).confirmDelivery(deliveryPersonId, mealKitId);
    }

    public MealKit recallDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        return getCargo(cargoId).recallDelivery(deliveryPersonId, mealKitId);
    }

    public void addDeliveryPerson(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        deliveryPersonIds.add(deliveryPersonId);
    }

    public List<DeliveryPersonUniqueIdentifier> getDeliveryPeople() {
        return deliveryPersonIds;
    }

    public void moveMealKitFromCargosToPending(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKitRemovedFromCargo = removeMealKitFromCargos(mealKitId);
        lockerAssignator.unassignLocker(mealKitRemovedFromCargo);
        pendingMealKits.put(mealKitId, mealKitRemovedFromCargo);
    }

    private MealKit removeMealKitFromCargos(MealKitUniqueIdentifier mealKitId) {
        for (Cargo cargo : cargos) {
            if (cargo.containsMealKit(mealKitId)) {
                MealKit mealKit = cargo.removeMealKit(mealKitId);
                if (cargo.isEmpty()) {
                    cargos.remove(cargo);
                }
                return mealKit;
            }
        }
        throw new InvalidMealKitIdException();
    }

    public MealKit getCargoMealKit(CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        return getCargo(cargoId).getMealKit(mealKitId);
    }

    public void removeMealKitFromLocker(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKitRemovedFromCargo = removeMealKitFromCargos(mealKitId);
        lockerAssignator.unassignLocker(mealKitRemovedFromCargo);
    }

    private Cargo getCargo(CargoUniqueIdentifier cargoId) {
        return cargos.stream().filter(cargo -> cargo.getCargoId().equals(cargoId)).findFirst().orElseThrow(InvalidCargoIdException::new);
    }
}
