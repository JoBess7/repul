package ca.ulaval.glo4003.repul.cooking.domain.exception;

public class MealKitNotCookedException extends CookingException {
    public MealKitNotCookedException() {
        super("This meal kit has not been cooked yet.");
    }
}
