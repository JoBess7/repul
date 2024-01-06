package ca.ulaval.glo4003.repul.lockerauthorization.application.exception;

import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.LockerAuthorizationException;

public class LockerAuthorizationSystemNotFoundException extends LockerAuthorizationException {
    public LockerAuthorizationSystemNotFoundException() {
        super("There is currently no initialized locker authorization system.");
    }
}
