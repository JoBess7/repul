package ca.ulaval.glo4003.repul.small.lockerauthorization.domain;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.LockerNotAssignedException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.NoCardLinkedToUserException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.UserCardNotAuthorizedException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LockerAuthorizationSystemTest {
    private static final SubscriberUniqueIdentifier AN_USER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final UserCardNumber A_USER_CARD_NUMBER = new UserCardNumber("123456789");
    private static final UserCardNumber ANOTHER_USER_CARD_NUMBER = new UserCardNumber("987654321");
    private static final LockerId A_LOCKER_ID = new LockerId("1234");

    @Test
    public void givenUserWithNoCardLinked_whenAuthorize_shouldThrowNoCardLinkedToUserException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);

        assertThrows(NoCardLinkedToUserException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER));
    }

    @Test
    public void givenLockerIdNotLinkedToOrder_whenAuthorize_shouldThrowLockerNotFoundException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);

        assertThrows(LockerNotAssignedException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER));
    }

    @Test
    public void givenLockerIdThatHasBeenUnassignedFromOrder_whenAuthorize_shouldThrowLockerNotFoundException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.unassignLocker(A_MEAL_KIT_ID);

        assertThrows(LockerNotAssignedException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER));
    }

    @Test
    public void givenNotAuthorizedUserCardNumber_whenAuthorize_shouldThrowUserCardNotAuthorizedException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.registerSubscriberCardNumber(AN_USER_ID, A_USER_CARD_NUMBER);

        assertThrows(UserCardNotAuthorizedException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, ANOTHER_USER_CARD_NUMBER));
    }

    @Test
    public void givenAuthorizedUserCardNumberAndCompleteInformation_whenAuthorize_shouldNotThrow() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.registerSubscriberCardNumber(AN_USER_ID, A_USER_CARD_NUMBER);

        assertDoesNotThrow(() -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER));
    }

    @Test
    public void givenAuthorizedUserCardNumber_whenAuthorize_shouldRemoveOrderFromList() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.registerSubscriberCardNumber(AN_USER_ID, A_USER_CARD_NUMBER);

        lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER);

        assertThrows(LockerNotAssignedException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER));
    }

    @Test
    public void givenNoOrder_whenAssigningLocker_shouldThrowOrderNotFoundException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();

        assertThrows(OrderNotFoundException.class, () -> lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID));
    }

    @Test
    public void givenNoOrder_whenUnassigningLocker_shouldThrowOrderNotFoundException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();

        assertThrows(OrderNotFoundException.class, () -> lockerAuthorizationSystem.unassignLocker(A_MEAL_KIT_ID));
    }
}
