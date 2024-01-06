package ca.ulaval.glo4003.repul.cooking.application.payload;

import java.util.List;

public record MealKitsPayload(
    List<MealKitPayload> mealKitPayloads
) {
}
