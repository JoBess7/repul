package ca.ulaval.glo4003.repul.delivery.api.response;

import java.util.List;

public record CargoResponse(
    String cargoId,
    String kitchenLocationId,
    List<MealKitResponse> mealKitsResponse
) {
}
