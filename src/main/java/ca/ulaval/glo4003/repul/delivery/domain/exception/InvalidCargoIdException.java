package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class InvalidCargoIdException extends DeliveryException {
    public InvalidCargoIdException() {
        super("The given cargo id is invalid.");
    }
}
