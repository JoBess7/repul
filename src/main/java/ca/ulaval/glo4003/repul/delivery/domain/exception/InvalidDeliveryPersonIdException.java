package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class InvalidDeliveryPersonIdException extends DeliveryException {
    public InvalidDeliveryPersonIdException() {
        super("The given delivery person id is invalid.");
    }
}
