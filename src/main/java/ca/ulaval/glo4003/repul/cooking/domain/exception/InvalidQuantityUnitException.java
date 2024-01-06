package ca.ulaval.glo4003.repul.cooking.domain.exception;

public class InvalidQuantityUnitException extends CookingException {
    public InvalidQuantityUnitException() {
        super("The given quantity unit is invalid.");
    }
}
