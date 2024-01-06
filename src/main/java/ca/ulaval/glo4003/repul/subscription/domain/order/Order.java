package ca.ulaval.glo4003.repul.subscription.domain.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeConfirmedException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeDeclinedException;

public class Order {
    private static final int DAYS_TO_CONFIRM = 2;
    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0);
    private final LocalDate deliveryDate;
    private final MealKitUniqueIdentifier orderId;
    private final MealKitType mealKitType;
    private OrderStatus orderStatus;

    public Order(MealKitUniqueIdentifier orderId, MealKitType mealKitType, LocalDate deliveryDate, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.mealKitType = mealKitType;
        this.orderStatus = orderStatus;
        this.deliveryDate = deliveryDate;
    }

    public Order(MealKitUniqueIdentifier orderId, MealKitType mealKitType, LocalDate deliveryDate) {
        this(orderId, mealKitType, deliveryDate, OrderStatus.PENDING);
    }

    public MealKitType getMealKitType() {
        return mealKitType;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public MealKitUniqueIdentifier getOrderId() {
        return orderId;
    }

    public boolean isInTheFuture() {
        LocalDate today = LocalDate.now();
        return deliveryDate.isAfter(today) || deliveryDate.isEqual(today);
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void confirm() {
        if (!canBeConfirmedOrDeclined()) {
            throw new OrderCannotBeConfirmedException();
        }
        orderStatus = OrderStatus.CONFIRMED;
    }

    public void decline() {
        if (!canBeConfirmedOrDeclined()) {
            throw new OrderCannotBeDeclinedException();
        }
        orderStatus = OrderStatus.DECLINED;
    }

    private boolean canBeConfirmedOrDeclined() {
        return !isAfterFinalConfirmationDateTime() &&
            (orderStatus == OrderStatus.PENDING || orderStatus == OrderStatus.DECLINED || orderStatus == OrderStatus.CONFIRMED);
    }

    public void markAsToCook() {
        orderStatus = OrderStatus.TO_COOK;
    }

    public void markAsToDeliver() {
        orderStatus = OrderStatus.TO_DELIVER;
    }

    public void markAsInDelivery() {
        orderStatus = OrderStatus.IN_DELIVERY;
    }

    public void markAsToPickUp() {
        orderStatus = OrderStatus.TO_PICKUP;
    }

    public void markAsPickedUp() {
        orderStatus = OrderStatus.PICKED_UP;
    }

    public void markAsDeclined() {
        orderStatus = OrderStatus.DECLINED;
    }

    public boolean isReadyToCook() {
        return orderStatus == OrderStatus.CONFIRMED && isAfterFinalConfirmationDateTime();
    }

    public boolean isOverdue() {
        return orderStatus == OrderStatus.PENDING && isAfterFinalConfirmationDateTime();
    }

    private boolean isAfterFinalConfirmationDateTime() {
        LocalDateTime finalConfirmationDateTime = LocalDateTime.of(this.deliveryDate.minusDays(DAYS_TO_CONFIRM), OPENING_TIME);

        return LocalDateTime.now().isAfter(finalConfirmationDateTime);
    }
}
