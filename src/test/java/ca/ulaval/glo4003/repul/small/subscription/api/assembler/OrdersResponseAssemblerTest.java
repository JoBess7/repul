package ca.ulaval.glo4003.repul.small.subscription.api.assembler;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.api.assembler.OrdersResponseAssembler;
import ca.ulaval.glo4003.repul.subscription.api.response.OrderResponse;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrderPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrdersResponseAssemblerTest {
    private static final LocalDate ORDER_DELIVERY_DATE = LocalDate.now().plusDays(4);
    private static final OrderStatus ORDER_STATUS = OrderStatus.PENDING;
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final MealKitUniqueIdentifier AN_ORDER_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final OrdersPayload AN_ORDERS_PAYLOAD =
        new OrdersPayload(List.of(new OrderPayload(AN_ORDER_ID, A_MEAL_KIT_TYPE, ORDER_DELIVERY_DATE, ORDER_STATUS)));

    @Test
    public void givenValidOrdersPayload_whenToOrdersResponse_shouldReturnListOfOrderResponse() {
        OrdersResponseAssembler ordersResponseAssembler = new OrdersResponseAssembler();

        List<OrderResponse> orderResponses = ordersResponseAssembler.toOrdersResponse(AN_ORDERS_PAYLOAD);

        assertEquals(ORDER_DELIVERY_DATE.toString(), orderResponses.get(0).deliveryDate());
        assertEquals(ORDER_STATUS.toString(), orderResponses.get(0).orderStatus());
        assertEquals(A_MEAL_KIT_TYPE.toString(), orderResponses.get(0).mealKitType());
    }
}
