package ca.ulaval.glo4003.repul.subscription.application.payload;

import java.util.List;

import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

public record OrdersPayload(
    List<OrderPayload> orders
) {
    public static OrdersPayload from(List<Order> orders) {
        return new OrdersPayload(orders.stream().map(OrderPayload::from).toList());
    }
}
