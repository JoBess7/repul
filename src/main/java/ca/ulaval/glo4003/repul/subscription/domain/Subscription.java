package ca.ulaval.glo4003.repul.subscription.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoNextOrderInSubscriptionException;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

public class Subscription {
    private final SubscriberUniqueIdentifier subscriberId;
    private final SubscriptionUniqueIdentifier subscriptionId;
    private final List<Order> orders;
    private final Frequency frequency;
    private final DeliveryLocationId deliveryLocationId;
    private final LocalDate startDate;
    private final Semester semester;
    private final MealKitType mealKitType;

    public Subscription(SubscriptionUniqueIdentifier subscriptionId, SubscriberUniqueIdentifier subscriberId, List<Order> orders, Frequency frequency,
                        DeliveryLocationId deliveryLocationId, LocalDate startDate, Semester semester, MealKitType mealKitType) {
        this.subscriptionId = subscriptionId;
        this.subscriberId = subscriberId;
        this.orders = orders;
        this.frequency = frequency;
        this.deliveryLocationId = deliveryLocationId;
        this.startDate = startDate;
        this.semester = semester;
        this.mealKitType = mealKitType;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public SubscriptionUniqueIdentifier getSubscriptionId() {
        return subscriptionId;
    }

    public SubscriberUniqueIdentifier getSubscriberId() {
        return subscriberId;
    }

    public DeliveryLocationId getDeliveryLocationId() {
        return deliveryLocationId;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Semester getSemester() {
        return semester;
    }

    public MealKitType getMealKitType() {
        return mealKitType;
    }

    public Order confirmNextMealKit() {
        Order order = this.findNextOrder();
        order.confirm();
        return order;
    }

    public Order declineNextMealKit() {
        Order order = this.findNextOrder();
        order.decline();
        return order;
    }

    private Order findNextOrder() {
        return orders.stream().filter(Order::isInTheFuture).findFirst().orElseThrow(NoNextOrderInSubscriptionException::new);
    }

    public Optional<Order> findCurrentOrder() {
        return orders.stream().filter(Order::isInTheFuture).findFirst();
    }

    public void markCurrentOrderAsToCook() {
        this.findCurrentOrder().ifPresent(Order::markAsToCook);
    }

    public void markCurrentOrderAsToDeliver() {
        this.findCurrentOrder().ifPresent(Order::markAsToDeliver);
    }

    public void markCurrentOrderAsInDelivery() {
        this.findCurrentOrder().ifPresent(Order::markAsInDelivery);
    }

    public void markCurrentOrderAsToPickUp() {
        this.findCurrentOrder().ifPresent(Order::markAsToPickUp);
    }

    public void markOrderAsPickedUp(MealKitUniqueIdentifier mealKitId) {
        this.orders.stream().filter(order -> order.getOrderId().equals(mealKitId)).findFirst().ifPresent(Order::markAsPickedUp);
    }

    public List<Order> getOrdersReadyToCook() {
        return orders.stream().filter(Order::isReadyToCook).toList();
    }

    public List<Order> getOverdueOrders() {
        return orders.stream().filter(Order::isOverdue).toList();
    }
}
