package ca.ulaval.glo4003.repul.lockerauthorization.application;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.lockerauthorization.api.query.OpenLockerQuery;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;

public class LockerAuthorizationService {
    private final LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository;
    private final RepULEventBus eventBus;

    public LockerAuthorizationService(RepULEventBus eventBus, LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository) {
        this.lockerAuthorizationSystemRepository = lockerAuthorizationSystemRepository;
        this.eventBus = eventBus;
    }

    public void createOrder(SubscriberUniqueIdentifier subscriberId, MealKitUniqueIdentifier mealKitId) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get();

        lockerAuthorizationSystem.createOrder(subscriberId, mealKitId);

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    public void assignLockerToMealKit(MealKitUniqueIdentifier mealKitId, LockerId lockerId) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get();

        lockerAuthorizationSystem.assignLocker(mealKitId, lockerId);

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    public void unassignLocker(MealKitUniqueIdentifier mealKitId) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get();

        lockerAuthorizationSystem.unassignLocker(mealKitId);

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    public void registerSubscriberCardNumber(SubscriberUniqueIdentifier subscriberId, UserCardNumber userCardNumber) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get();

        lockerAuthorizationSystem.registerSubscriberCardNumber(subscriberId, userCardNumber);

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    public void openLocker(OpenLockerQuery openLockerQuery) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get();

        MealKitUniqueIdentifier mealKitId = lockerAuthorizationSystem.authorize(openLockerQuery.lockerId(), openLockerQuery.userCardNumber());

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);

        eventBus.publish(new MealKitPickedUpByUserEvent(mealKitId));
    }
}
