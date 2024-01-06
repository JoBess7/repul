package ca.ulaval.glo4003.repul.subscription.api.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.subscription.api.response.OrderResponse;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrderPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;

public class OrdersResponseAssembler {
    public List<OrderResponse> toOrdersResponse(OrdersPayload ordersPayload) {
        return ordersPayload.orders().stream()
            .map(this::toOrderResponse).toList();
    }

    private OrderResponse toOrderResponse(OrderPayload order) {
        return new OrderResponse(
            order.mealKitType().toString(),
            order.deliveryDate().toString(),
            order.orderStatus().toString());
    }

}
