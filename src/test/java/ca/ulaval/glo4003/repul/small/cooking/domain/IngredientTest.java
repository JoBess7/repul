package ca.ulaval.glo4003.repul.small.cooking.domain;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.cooking.domain.Ingredient;
import ca.ulaval.glo4003.repul.cooking.domain.Quantity;
import ca.ulaval.glo4003.repul.fixture.cooking.IngredientFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IngredientTest {
    private static final Ingredient AN_INGREDIENT = new IngredientFixture().withName("Apple").withQuantityInGrams(5.00).build();
    private static final Quantity A_QUANTITY = new Quantity(5.00, "g");

    @Test
    public void givenIngredientsWithSameName_whenAddingQuantity_shouldAddQuantity() {
        Quantity expectedQuantity = new Quantity(AN_INGREDIENT.quantity().getValue() + A_QUANTITY.getValue(), AN_INGREDIENT.quantity().getUnit());

        Ingredient resultIngredient = AN_INGREDIENT.add(A_QUANTITY);

        assertEquals(expectedQuantity, resultIngredient.quantity());
    }
}
