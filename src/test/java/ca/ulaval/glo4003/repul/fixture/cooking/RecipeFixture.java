package ca.ulaval.glo4003.repul.fixture.cooking;

import java.util.List;

import ca.ulaval.glo4003.repul.cooking.domain.Ingredient;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;

public class RecipeFixture {
    private String name;
    private int calories;
    private List<Ingredient> ingredients;

    public RecipeFixture() {
        this.name = "Spaghetti Bolognese";
        this.calories = 10;
        this.ingredients = List.of(
            new IngredientFixture().build(),
            new IngredientFixture().withName("tomato").withUnitLessQuantity(2).build(),
            new IngredientFixture().withName("pasta").withQuantityInGrams(100).build()
        );
    }

    public RecipeFixture withName(String name) {
        this.name = name;
        return this;
    }

    public RecipeFixture withCalories(int calories) {
        this.calories = calories;
        return this;
    }

    public RecipeFixture withIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public RecipeFixture addNewIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public Recipe build() {
        return new Recipe(name, calories, ingredients);
    }
}
