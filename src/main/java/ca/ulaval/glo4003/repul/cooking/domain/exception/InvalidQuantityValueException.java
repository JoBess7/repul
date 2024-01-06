package ca.ulaval.glo4003.repul.cooking.domain.exception;

public class InvalidQuantityValueException extends CookingException {
    public InvalidQuantityValueException() {
        super("The given quantity value is invalid.");
    }
}
