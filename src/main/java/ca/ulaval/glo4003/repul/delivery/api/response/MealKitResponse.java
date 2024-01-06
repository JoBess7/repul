package ca.ulaval.glo4003.repul.delivery.api.response;

public record MealKitResponse(
    String mealKitId,
    String deliveryLocationId,
    String lockerNumber
) {
}
