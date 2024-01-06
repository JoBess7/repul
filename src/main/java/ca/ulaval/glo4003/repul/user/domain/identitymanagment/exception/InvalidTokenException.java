package ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class InvalidTokenException extends UserException {
    public InvalidTokenException() {
        super("The token is invalid.");
    }
}
