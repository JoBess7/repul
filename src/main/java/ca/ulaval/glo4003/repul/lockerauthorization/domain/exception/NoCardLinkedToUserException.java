package ca.ulaval.glo4003.repul.lockerauthorization.domain.exception;

public class NoCardLinkedToUserException extends LockerAuthorizationException {
    public NoCardLinkedToUserException() {
        super("No card is linked to this user.");
    }
}
