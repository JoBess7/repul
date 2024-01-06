package ca.ulaval.glo4003.repul.fixture.cooking;

import ca.ulaval.glo4003.repul.cooking.domain.Ingredient;
import ca.ulaval.glo4003.repul.cooking.domain.Quantity;

public class IngredientFixture {
    private String name;
    private Quantity quantity;

    public IngredientFixture() {
        this.name = "butter";
        this.quantity = new Quantity(1.00, "kg");
    }

    public IngredientFixture withName(String name) {
        this.name = name;
        return this;
    }

    public IngredientFixture withQuantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }

    public IngredientFixture withQuantityInGrams(double quantity) {
        this.quantity = new Quantity(quantity, "g");
        return this;
    }

    public IngredientFixture withQuantityInML(double quantity) {
        this.quantity = new Quantity(quantity, "mL");
        return this;
    }

    public IngredientFixture withQuantityInTsp(double quantity) {
        this.quantity = new Quantity(quantity, "Tsp");
        return this;
    }

    public IngredientFixture withUnitLessQuantity(double quantity) {
        this.quantity = new Quantity(quantity, "");
        return this;
    }

    public Ingredient build() {
        return new Ingredient(name, quantity);
    }
}
