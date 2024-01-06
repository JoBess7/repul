package ca.ulaval.glo4003.repul.small.cooking.api.assembler;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.cooking.api.assembler.MealKitsResponseAssembler;
import ca.ulaval.glo4003.repul.cooking.api.response.IngredientResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.MealKitResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.QuantityResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.RecipeResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.ToCookResponse;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitPayload;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.cooking.domain.Ingredient;
import ca.ulaval.glo4003.repul.cooking.domain.MealKit;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;
import ca.ulaval.glo4003.repul.fixture.cooking.IngredientFixture;
import ca.ulaval.glo4003.repul.fixture.cooking.MealKitFixture;
import ca.ulaval.glo4003.repul.fixture.cooking.RecipeFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MealKitsResponseAssemblerTest {
    private static final List<Ingredient> DEFAULT_INGREDIENT = List.of(new IngredientFixture().build());
    private static final MealKit A_MEALKIT = new MealKitFixture().withRecipes(List.of(new RecipeFixture().withIngredients(DEFAULT_INGREDIENT).build())).build();
    private static final MealKitPayload A_MEAL_KIT_PAYLOAD = new MealKitPayload(A_MEALKIT.getMealKitId(), A_MEALKIT.getDateOfReceipt(), A_MEALKIT.getRecipes());
    private MealKitsResponseAssembler mealKitsResponseAssembler;

    @BeforeEach
    public void createMealKitsResponseAssembler() {
        this.mealKitsResponseAssembler = new MealKitsResponseAssembler();
    }

    @Test
    public void givenAMealKit_whenAssemblingResponse_shouldReturnToCookResponseWithMealKitsAndIngredient() {
        Recipe mealKitRecipe = A_MEALKIT.getRecipes().get(0);
        IngredientResponse expectedIngredientResponse = new IngredientResponse(mealKitRecipe.ingredients().get(0).ingredient(),
            new QuantityResponse(mealKitRecipe.ingredients().get(0).quantity().getValue(), mealKitRecipe.ingredients().get(0).quantity().getUnit()));
        MealKitResponse expectedMealKitsResponse = new MealKitResponse(A_MEALKIT.getMealKitId().getUUID().toString(),
            List.of(new RecipeResponse(mealKitRecipe.name(), mealKitRecipe.calories(), List.of(expectedIngredientResponse))),
            A_MEALKIT.getDateOfReceipt().toString());

        ToCookResponse toCookResponse = mealKitsResponseAssembler.toToCookResponse(new MealKitsPayload(List.of(A_MEAL_KIT_PAYLOAD)));

        assertEquals(List.of(expectedMealKitsResponse), toCookResponse.mealKits());
        assertEquals(List.of(expectedIngredientResponse), toCookResponse.totalIngredients());
    }

    @Test
    public void givenMultipleMealKitWithSameIngredient_whenAssemblingResponse_shouldAddIngredientQuantity() {
        Recipe mealKitRecipe = A_MEALKIT.getRecipes().get(0);
        IngredientResponse expectedIngredientResponse = new IngredientResponse(mealKitRecipe.ingredients().get(0).ingredient(),
            new QuantityResponse(mealKitRecipe.ingredients().get(0).quantity().getValue() * 2, mealKitRecipe.ingredients().get(0).quantity().getUnit()));
        MealKitResponse expectedMealKitsResponse = new MealKitResponse(A_MEALKIT.getMealKitId().getUUID().toString(), List.of(
            new RecipeResponse(mealKitRecipe.name(), mealKitRecipe.calories(), List.of(new IngredientResponse(mealKitRecipe.ingredients().get(0).ingredient(),
                new QuantityResponse(mealKitRecipe.ingredients().get(0).quantity().getValue(), mealKitRecipe.ingredients().get(0).quantity().getUnit()))))),
            A_MEALKIT.getDateOfReceipt().toString());

        ToCookResponse toCookResponse = mealKitsResponseAssembler.toToCookResponse(new MealKitsPayload(List.of(A_MEAL_KIT_PAYLOAD, A_MEAL_KIT_PAYLOAD)));

        assertEquals(List.of(expectedMealKitsResponse, expectedMealKitsResponse), toCookResponse.mealKits());
        assertEquals(List.of(expectedIngredientResponse), toCookResponse.totalIngredients());
    }
}
