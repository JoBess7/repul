package ca.ulaval.glo4003.repul.cooking.domain.exception;

public class InvalidRecipeException extends CookingException {
    public InvalidRecipeException() {
        super("The given recipe is invalid.");
    }
}
