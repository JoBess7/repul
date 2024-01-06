package ca.ulaval.glo4003.repul.cooking.domain.exception;

public class KitchenNotFoundException extends CookingException {
    public KitchenNotFoundException() {
        super("Kitchen was not found.");
    }
}
