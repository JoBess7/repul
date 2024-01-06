package ca.ulaval.glo4003.repul.cooking.api.response;

import java.util.List;

public record ToCookResponse(
    List<MealKitResponse> mealKits,
    List<IngredientResponse> totalIngredients
) {
}
