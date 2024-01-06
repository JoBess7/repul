package ca.ulaval.glo4003.repul.notification.application;

import java.time.LocalTime;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.notification.domain.NotificationMessage;

public class MessageFactory {

    public NotificationMessage createReadyToBeDeliveredMessage(CargoUniqueIdentifier cargoId, KitchenLocationId locationId, List<MealKitDto> mealKitDtos) {
        String messageBody = "Your meal kits (cargo id: " + cargoId.getUUID().toString() + ") are ready to be fetched from " + locationId.toString() + ".\n";
        messageBody += "Here is the list of meal kits to be delivered:\n";
        for (MealKitDto mealKitDto : mealKitDtos) {
            String lockerId;
            if (mealKitDto.lockerId().isPresent()) {
                lockerId = Integer.toString(mealKitDto.lockerId().get().lockerNumber());
            } else {
                lockerId = "To Be Determined";
            }
            messageBody +=
                "MealKit ID " + mealKitDto.mealKitId().getUUID() + " to " + mealKitDto.deliveryLocationId().toString() + " in box " + lockerId + "\n";
        }
        String messageTitle = "RepUL - Meal kits ready to be delivered";
        return new NotificationMessage(messageTitle, messageBody);
    }

    public NotificationMessage createDeliveredMessage(MealKitUniqueIdentifier mealKitId, DeliveryLocationId deliveryLocationId, LocalTime localTime,
                                                      LockerId lockerId) {
        String messageBody = "Your meal kit with id " + mealKitId.getUUID().toString();
        messageBody += " is ready to be fetched from " + deliveryLocationId.toString();
        messageBody += " in the locker " + lockerId.lockerNumber();
        messageBody += ".\n";
        messageBody += "It arrived at " + localTime.toString() + ".\n";
        messageBody += "Come get it!";
        String messageTitle = "RepUL - Meal kit ready to be fetched";
        return new NotificationMessage(messageTitle, messageBody);
    }

    public NotificationMessage createMealKitAvailableForPickUpMessage(MealKitUniqueIdentifier mealKitId, KitchenLocationId kitchenLocationId) {
        String messageBody = "Your meal kit with id " + mealKitId.getUUID().toString();
        messageBody += " is ready to be fetched from the " + kitchenLocationId.toString();
        messageBody += " kitchen.\n";
        messageBody += "Come get it!";
        String messageTitle = "RepUL - Meal kit ready to be fetched from kitchen";
        return new NotificationMessage(messageTitle, messageBody);
    }
}
