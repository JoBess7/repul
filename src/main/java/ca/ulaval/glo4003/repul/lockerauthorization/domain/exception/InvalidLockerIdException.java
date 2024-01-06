package ca.ulaval.glo4003.repul.lockerauthorization.domain.exception;

public class InvalidLockerIdException extends LockerAuthorizationException {
    public InvalidLockerIdException() {
        super("The locker id cannot be null or blank.");
    }
}
