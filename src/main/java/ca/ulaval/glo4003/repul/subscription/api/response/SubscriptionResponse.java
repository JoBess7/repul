package ca.ulaval.glo4003.repul.subscription.api.response;

public record SubscriptionResponse(
    String subscriptionId,
    String dayOfWeek,
    String locationId,
    String mealKitType,
    String startDate,
    String semesterCode
) {
}
