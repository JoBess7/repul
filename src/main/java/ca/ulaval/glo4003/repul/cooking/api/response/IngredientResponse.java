package ca.ulaval.glo4003.repul.cooking.api.response;

public record IngredientResponse(
    String name,
    QuantityResponse quantity
) {
}
