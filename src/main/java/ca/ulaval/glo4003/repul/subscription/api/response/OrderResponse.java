package ca.ulaval.glo4003.repul.subscription.api.response;

public record OrderResponse(
    String mealKitType,
    String deliveryDate,
    String orderStatus
) {
}
