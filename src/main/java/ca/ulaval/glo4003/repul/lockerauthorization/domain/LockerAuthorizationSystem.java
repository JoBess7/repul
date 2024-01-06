package ca.ulaval.glo4003.repul.lockerauthorization.domain;

import java.util.HashMap;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.LockerNotAssignedException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.NoCardLinkedToUserException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.UserCardNotAuthorizedException;

public class LockerAuthorizationSystem {
    private final Map<UniqueIdentifier, UserCardNumber> userCardNumberByUser = new HashMap<>();
    private final Map<UniqueIdentifier, Order> orders = new HashMap<>();

    public void registerSubscriberCardNumber(UniqueIdentifier userId, UserCardNumber userCardNumber) {
        userCardNumberByUser.put(userId, userCardNumber);
    }

    public void createOrder(SubscriberUniqueIdentifier userId, MealKitUniqueIdentifier mealKitId) {
        orders.put(mealKitId, new Order(userId, mealKitId));
    }

    public void assignLocker(UniqueIdentifier mealKitId, LockerId lockerId) {
        Order order = getOrderFrom(mealKitId);
        order.assignLocker(lockerId);
    }

    public void unassignLocker(UniqueIdentifier mealKitId) {
        Order order = getOrderFrom(mealKitId);
        order.unassignLocker();
    }

    public MealKitUniqueIdentifier authorize(LockerId lockerId, UserCardNumber userCardNumberToValidate) {
        Order order = getOrderByLockerId(lockerId);
        if (userCardNumberByUser.containsKey(order.getUserId())) {
            UserCardNumber userCardNumber = userCardNumberByUser.get(order.getUserId());
            if (userCardNumber.equals(userCardNumberToValidate)) {
                removeTakenOrder(order);
            } else {
                throw new UserCardNotAuthorizedException();
            }
        } else {
            throw new NoCardLinkedToUserException();
        }
        return order.getMealKitId();
    }

    private Order getOrderByLockerId(LockerId lockerId) {
        return orders.values().stream().filter(order -> order.hasLockerId(lockerId))
            .findFirst().orElseThrow(LockerNotAssignedException::new);
    }

    private void removeTakenOrder(Order order) {
        orders.remove(order.getMealKitId());
    }

    private Order getOrderFrom(UniqueIdentifier mealKitId) {
        Order order = orders.get(mealKitId);
        if (order == null) {
            throw new OrderNotFoundException();
        }
        return order;
    }
}
