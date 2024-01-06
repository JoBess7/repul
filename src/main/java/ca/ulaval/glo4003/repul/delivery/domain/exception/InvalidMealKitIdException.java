package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class InvalidMealKitIdException extends DeliveryException {
    public InvalidMealKitIdException() {
        super("This meal kit id is invalid.");
    }
}
