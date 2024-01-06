package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;

public class LockerAssignator {
    private final Map<DeliveryLocation, List<MealKit>> waitingMealKitsByDeliveryLocation;

    public LockerAssignator(List<DeliveryLocation> deliveryLocations) {
        this.waitingMealKitsByDeliveryLocation = new HashMap();
        deliveryLocations.forEach(deliveryLocation -> waitingMealKitsByDeliveryLocation.put(deliveryLocation, new ArrayList<>()));
    }

    public void assignLocker(MealKit mealKit) {
        DeliveryLocation deliveryLocation = mealKit.getDeliveryLocation();

        Optional<Locker> availableLocker = deliveryLocation.findNextAvailableLocker();

        if (availableLocker.isPresent()) {
            availableLocker.get().assign(mealKit.getMealKitId());
            LockerId lockerId = availableLocker.get().getLockerId();
            mealKit.assignLocker(Optional.of(lockerId));
        } else {
            waitingMealKitsByDeliveryLocation.get(deliveryLocation).add(mealKit);
        }
    }

    public void unassignLocker(MealKit mealKit) {
        DeliveryLocation deliveryLocation = mealKit.getDeliveryLocation();
        Locker locker = deliveryLocation.findLockerById(mealKit.getLockerId().get());
        locker.unassign();

        if (waitingMealKitsByDeliveryLocation.get(deliveryLocation).size() > 0) {
            this.assignLocker(waitingMealKitsByDeliveryLocation.get(deliveryLocation).get(0));
        }
    }
}
