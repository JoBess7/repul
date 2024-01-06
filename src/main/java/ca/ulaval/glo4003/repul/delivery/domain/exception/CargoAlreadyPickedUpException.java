package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class CargoAlreadyPickedUpException extends DeliveryException {
    public CargoAlreadyPickedUpException() {
        super("The cargo is already picked up for delivery.");
    }
}
