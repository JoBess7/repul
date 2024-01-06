package ca.ulaval.glo4003.repul.cooking.domain;

public record Ingredient(
    String ingredient,
    Quantity quantity
) {
    public Ingredient add(Quantity quantity) {
        return new Ingredient(ingredient, this.quantity.add(quantity));
    }
}
