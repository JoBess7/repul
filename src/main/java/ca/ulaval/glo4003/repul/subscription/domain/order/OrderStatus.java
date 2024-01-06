package ca.ulaval.glo4003.repul.subscription.domain.order;

public enum OrderStatus {
    PENDING,
    DECLINED,
    CONFIRMED,
    TO_COOK,
    TO_DELIVER,
    IN_DELIVERY,
    TO_PICKUP,
    PICKED_UP
}
