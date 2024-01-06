package ca.ulaval.glo4003.repul.cooking.domain;

import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidMealKitTypeException;

public class RecipesCatalog {
    private static RecipesCatalog instance;
    private Map<MealKitType, List<Recipe>> recipes = Map.of();

    public static RecipesCatalog getInstance() {
        if (instance == null) {
            instance = new RecipesCatalog();
        }
        return instance;
    }

    public List<Recipe> getRecipes(MealKitType mealKitType) {
        if (recipes.containsKey(mealKitType)) {
            return recipes.get(mealKitType);
        }
        throw new InvalidMealKitTypeException();
    }

    public void setRecipes(Map<MealKitType, List<Recipe>> recipes) {
        this.recipes = recipes;
    }
}
