package ca.ulaval.glo4003.repul.lockerauthorization.domain.exception;

public class LockerNotAssignedException extends LockerAuthorizationException {
    public LockerNotAssignedException() {
        super("This locker is not assigned.");
    }
}
