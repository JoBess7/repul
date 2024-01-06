package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.exception.CargoAlreadyPickedUpException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidDeliveryPersonIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotInCargoException;

public class Cargo {
    private final CargoUniqueIdentifier cargoId;
    private final List<MealKit> mealKits;
    private final KitchenLocation kitchenLocation;
    private Optional<DeliveryPersonUniqueIdentifier> deliveryPersonId = Optional.empty();

    public Cargo(CargoUniqueIdentifier cargoId, KitchenLocation kitchenLocation, List<MealKit> mealKits) {
        this.cargoId = cargoId;
        this.kitchenLocation = kitchenLocation;
        this.mealKits = mealKits;
    }

    public CargoUniqueIdentifier getCargoId() {
        return cargoId;
    }

    public Optional<DeliveryPersonUniqueIdentifier> getDeliveryPersonId() {
        return deliveryPersonId;
    }

    public KitchenLocation getKitchenLocation() {
        return kitchenLocation;
    }

    public List<MealKit> getMealKits() {
        return mealKits;
    }

    public boolean isReadyToBeDelivered() {
        return deliveryPersonId.isEmpty();
    }

    public List<MealKit> pickupCargo(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        if (this.deliveryPersonId.isPresent()) {
            throw new CargoAlreadyPickedUpException();
        }
        this.deliveryPersonId = Optional.of(deliveryPersonId);
        mealKits.forEach(MealKit::pickupReadyToDeliverMealKit);
        return mealKits;
    }

    public List<MealKit> cancelCargo(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        validateIsSameDeliveryPersonId(deliveryPersonId);

        this.deliveryPersonId = Optional.empty();

        List<MealKit> notYetDeliveredMealKits = mealKits.stream().filter(MealKit::isNotAlreadyDelivered).toList();
        notYetDeliveredMealKits.forEach(MealKit::cancelDelivery);
        return notYetDeliveredMealKits;
    }

    public void confirmDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, MealKitUniqueIdentifier mealKitId) {
        validateIsSameDeliveryPersonId(deliveryPersonId);

        MealKit mealKitToConfirm = mealKits.stream().filter(mealKit -> mealKit.getMealKitId().equals(mealKitId))
            .findFirst().orElseThrow(MealKitNotInCargoException::new);

        mealKitToConfirm.confirmDelivery();
    }

    public MealKit recallDelivery(DeliveryPersonUniqueIdentifier deliveryPersonId, MealKitUniqueIdentifier mealKitId) {
        validateIsSameDeliveryPersonId(deliveryPersonId);

        MealKit mealKitToRecall = mealKits.stream().filter(mealKit -> mealKit.getMealKitId().equals(mealKitId))
            .findFirst().orElseThrow(MealKitNotInCargoException::new);

        mealKitToRecall.recallDelivery();
        return mealKitToRecall;
    }

    public boolean containsMealKit(MealKitUniqueIdentifier mealKitId) {
        return mealKits.stream().anyMatch(mealKit -> mealKit.getMealKitId().equals(mealKitId));
    }

    public MealKit removeMealKit(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = mealKits.stream().filter(currentMealKit -> currentMealKit.getMealKitId().equals(mealKitId))
            .findFirst().orElseThrow(InvalidMealKitIdException::new);

        mealKits.remove(mealKit);

        return mealKit;
    }

    public boolean isEmpty() {
        return mealKits.isEmpty();
    }

    private void validateIsSameDeliveryPersonId(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        if (!Optional.of(deliveryPersonId).equals(this.deliveryPersonId)) {
            throw new InvalidDeliveryPersonIdException();
        }
    }

    public MealKit getMealKit(MealKitUniqueIdentifier mealKitId) {
        return mealKits.stream().filter(mealKit -> mealKit.getMealKitId().equals(mealKitId)).findFirst().orElseThrow(InvalidMealKitIdException::new);
    }
}
