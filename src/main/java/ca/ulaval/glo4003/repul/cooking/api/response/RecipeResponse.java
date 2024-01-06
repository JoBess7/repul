package ca.ulaval.glo4003.repul.cooking.api.response;

import java.util.List;

public record RecipeResponse(
    String name,
    int calories,
    List<IngredientResponse> ingredients
) {
}
