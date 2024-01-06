package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class InvalidLockerIdException extends DeliveryException {
    public InvalidLockerIdException() {
        super("The given locker id is invalid.");
    }
}
