package ca.ulaval.glo4003.repul.lockerauthorization.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.exception.RepULException;

public abstract class LockerAuthorizationException extends RepULException {
    public LockerAuthorizationException(String message) {
        super(message);
    }
}
