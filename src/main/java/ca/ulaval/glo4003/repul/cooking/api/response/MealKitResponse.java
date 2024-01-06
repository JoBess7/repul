package ca.ulaval.glo4003.repul.cooking.api.response;

import java.util.List;

public record MealKitResponse(
    String mealKitId,
    List<RecipeResponse> recipes,
    String deliveryDate
) {
}
