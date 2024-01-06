package ca.ulaval.glo4003.repul.cooking.domain;

import java.util.List;

import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidRecipeException;

public record Recipe(
    String name,
    int calories,
    List<Ingredient> ingredients
) {
    public Recipe {
        if (name.isBlank() || calories <= 0 || ingredients.isEmpty()) {
            throw new InvalidRecipeException();
        }
    }
}
