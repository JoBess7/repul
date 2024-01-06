package ca.ulaval.glo4003.repul.user.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.exception.RepULException;

public abstract class UserException extends RepULException {
    public UserException(String message) {
        super(message);
    }
}
