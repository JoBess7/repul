package ca.ulaval.glo4003.repul.small.cooking.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.cooking.api.MealKitResource;
import ca.ulaval.glo4003.repul.cooking.api.assembler.MealKitsResponseAssembler;
import ca.ulaval.glo4003.repul.cooking.api.response.ToCookResponse;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitPayload;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.cooking.domain.Ingredient;
import ca.ulaval.glo4003.repul.cooking.domain.MealKit;
import ca.ulaval.glo4003.repul.fixture.cooking.IngredientFixture;
import ca.ulaval.glo4003.repul.fixture.cooking.MealKitFixture;
import ca.ulaval.glo4003.repul.fixture.cooking.RecipeFixture;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MealKitResourceTest {
    private static final List<Ingredient> DEFAULT_INGREDIENT = List.of(new IngredientFixture().build());
    private static final MealKit MEAL_KIT = new MealKitFixture().withRecipes(List.of(new RecipeFixture().withIngredients(DEFAULT_INGREDIENT).build())).build();
    private static final MealKitsPayload MEAL_KITS_PAYLOAD =
        new MealKitsPayload(List.of(new MealKitPayload(MEAL_KIT.getMealKitId(), MEAL_KIT.getDateOfReceipt(), MEAL_KIT.getRecipes())));
    private static final ToCookResponse A_TO_COOK_RESPONSE = new MealKitsResponseAssembler().toToCookResponse(MEAL_KITS_PAYLOAD);
    private MealKitResource mealKitResource;
    @Mock
    private CookingService cookingService;

    @BeforeEach
    public void createCookingResource() {
        mealKitResource = new MealKitResource(cookingService);
    }

    @Test
    public void whenGettingMealKitsToCook_shouldReturn200OkWithToCookResponse() {
        when(cookingService.getMealKitsToCook()).thenReturn(MEAL_KITS_PAYLOAD);

        Response response = mealKitResource.getMealKitsToCook();

        assertEquals(response.getStatus(), 200);
        assertEquals(response.getEntity(), A_TO_COOK_RESPONSE);
    }
}
