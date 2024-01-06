package ca.ulaval.glo4003.repul.delivery.api.response;

public record DeliveryLocationResponse(
    String locationId,
    String name,
    int remainingCapacity
) {
}
