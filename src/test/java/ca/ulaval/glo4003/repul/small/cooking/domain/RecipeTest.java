package ca.ulaval.glo4003.repul.small.cooking.domain;

import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.cooking.domain.Ingredient;
import ca.ulaval.glo4003.repul.cooking.domain.Quantity;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;
import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidRecipeException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecipeTest {
    private static final String A_NAME = "name";
    private static final String EMPTY_NAME = "";
    private static final int CALORIES = 1;
    private static final int NEGATIVE_CALORIES = -1;
    private static final List<Ingredient> INGREDIENTS = List.of(new Ingredient("ingredient", new Quantity(1, "kg")));
    private static final List<Ingredient> EMPTY_INGREDIENTS = List.of();

    @Test
    public void givenEmptyName_whenCreatingRecipe_shouldThrowInvalidRecipeException() {
        assertThrows(InvalidRecipeException.class, () -> new Recipe(EMPTY_NAME, CALORIES, INGREDIENTS));
    }

    @Test
    public void givenNegativeCalories_whenCreatingRecipe_shouldThrowInvalidRecipeException() {
        assertThrows(InvalidRecipeException.class, () -> new Recipe(A_NAME, NEGATIVE_CALORIES, INGREDIENTS));
    }

    @Test
    public void givenEmptyIngredients_whenCreatingRecipe_shouldThrowInvalidRecipeException() {
        assertThrows(InvalidRecipeException.class, () -> new Recipe(A_NAME, CALORIES, EMPTY_INGREDIENTS));
    }
}
