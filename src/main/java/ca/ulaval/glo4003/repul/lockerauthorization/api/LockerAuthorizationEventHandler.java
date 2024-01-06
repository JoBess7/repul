package ca.ulaval.glo4003.repul.lockerauthorization.api;

import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.UserCardAddedEvent;

import com.google.common.eventbus.Subscribe;

public class LockerAuthorizationEventHandler {
    private LockerAuthorizationService lockerAuthorizationService;

    public LockerAuthorizationEventHandler(LockerAuthorizationService lockerAuthorizationService) {
        this.lockerAuthorizationService = lockerAuthorizationService;
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent mealKitConfirmedEvent) {
        if (mealKitConfirmedEvent.deliveryLocationId.isPresent()) {
            lockerAuthorizationService.createOrder(mealKitConfirmedEvent.subscriberId, mealKitConfirmedEvent.mealKitId);
        }
    }

    @Subscribe
    public void handleConfirmedDeliveryEvent(ConfirmedDeliveryEvent confirmedDeliveryEvent) {
        lockerAuthorizationService.assignLockerToMealKit(confirmedDeliveryEvent.mealKitId, new LockerId(confirmedDeliveryEvent.lockerId.id()));
    }

    @Subscribe
    public void handleRecalledDeliveryEvent(RecalledDeliveryEvent recalledDeliveryEvent) {
        lockerAuthorizationService.unassignLocker(recalledDeliveryEvent.mealKitId);
    }

    @Subscribe
    public void handleUserCardAddedEvent(UserCardAddedEvent userCardAddedEvent) {
        lockerAuthorizationService.registerSubscriberCardNumber(userCardAddedEvent.subscriberId, userCardAddedEvent.userCardNumber);
    }
}
