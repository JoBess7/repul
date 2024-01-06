package ca.ulaval.glo4003.repul.lockerauthorization.domain.exception;

public class OrderNotFoundException extends LockerAuthorizationException {
    public OrderNotFoundException() {
        super("The order is not found.");
    }
}
