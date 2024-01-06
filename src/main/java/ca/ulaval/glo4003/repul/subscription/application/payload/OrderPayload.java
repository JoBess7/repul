package ca.ulaval.glo4003.repul.subscription.application.payload;

import java.time.LocalDate;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;

public record OrderPayload(MealKitUniqueIdentifier orderId, MealKitType mealKitType, LocalDate deliveryDate, OrderStatus orderStatus) {
    public static OrderPayload from(Order order) {
        return new OrderPayload(order.getOrderId(), order.getMealKitType(), order.getDeliveryDate(), order.getOrderStatus());
    }
}
