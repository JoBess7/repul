package ca.ulaval.glo4003.repul.cooking.api.assembler;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.api.response.IngredientResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.MealKitResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.QuantityResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.RecipeResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.SelectionResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.ToCookResponse;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitPayload;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.cooking.domain.Ingredient;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;

public class MealKitsResponseAssembler {
    public SelectionResponse toSelectionResponse(List<MealKitUniqueIdentifier> mealKitIds) {
        List<String> mealKitIdsAsString = mealKitIds.stream().map(id -> id.getUUID().toString()).toList();
        return new SelectionResponse(mealKitIdsAsString);
    }

    public ToCookResponse toToCookResponse(MealKitsPayload mealKitsPayload) {
        List<Ingredient> combinedIngredients = new ArrayList<>();
        List<Ingredient> allIngredients = getAllIngredients(mealKitsPayload);
        for (Ingredient ingredient : allIngredients) {
            boolean foundInList = false;
            for (Ingredient ingredientInCombinedIngredients : combinedIngredients) {
                if (ingredient.ingredient().equals(ingredientInCombinedIngredients.ingredient())) {
                    foundInList = true;
                    Ingredient ingredientWithNewQuantity = ingredientInCombinedIngredients.add(ingredient.quantity());
                    combinedIngredients.remove(ingredientInCombinedIngredients);
                    combinedIngredients.add(ingredientWithNewQuantity);
                    break;
                }
            }
            if (!foundInList) {
                combinedIngredients.add(ingredient);
            }
        }

        List<MealKitResponse> mealKitResponses = mealKitsPayload.mealKitPayloads().stream().map(this::toMealKitResponse).toList();
        List<IngredientResponse> combinedIngredientsResponses = combinedIngredients.stream().map(this::toIngredientResponse).toList();
        return new ToCookResponse(mealKitResponses, combinedIngredientsResponses);
    }

    private List<Ingredient> getAllIngredients(MealKitsPayload mealKitsPayload) {
        List<Ingredient> allIngredients = new ArrayList<>();
        for (MealKitPayload mealKitPayload : mealKitsPayload.mealKitPayloads()) {
            for (Recipe recipe : mealKitPayload.recipes()) {
                allIngredients.addAll(recipe.ingredients());
            }
        }
        return allIngredients;
    }

    private MealKitResponse toMealKitResponse(MealKitPayload mealKitPayload) {
        return new MealKitResponse(
            mealKitPayload.mealKitId().getUUID().toString(),
            mealKitPayload.recipes().stream().map(this::toRecipeResponse).toList(),
            mealKitPayload.deliveryDate().toString()
        );
    }

    private RecipeResponse toRecipeResponse(Recipe recipes) {
        return new RecipeResponse(
            recipes.name(),
            recipes.calories(),
            recipes.ingredients().stream().map(this::toIngredientResponse).toList()
        );
    }

    private IngredientResponse toIngredientResponse(Ingredient ingredient) {
        QuantityResponse quantityResponse = new QuantityResponse(ingredient.quantity().getValue(), ingredient.quantity().getUnit());
        return new IngredientResponse(ingredient.ingredient(), quantityResponse);
    }
}
