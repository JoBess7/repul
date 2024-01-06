package ca.ulaval.glo4003.repul.cooking.api.response;

import java.util.List;

public record SelectionResponse(
    List<String> mealKitSelectionIds
) {
}
