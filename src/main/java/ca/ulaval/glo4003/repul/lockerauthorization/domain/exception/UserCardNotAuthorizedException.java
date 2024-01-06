package ca.ulaval.glo4003.repul.lockerauthorization.domain.exception;

public class UserCardNotAuthorizedException extends LockerAuthorizationException {
    public UserCardNotAuthorizedException() {
        super("This card is not authorized to open this locker.");
    }
}
