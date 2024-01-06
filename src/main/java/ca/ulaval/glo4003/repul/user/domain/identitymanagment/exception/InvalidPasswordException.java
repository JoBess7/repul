package ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class InvalidPasswordException extends UserException {
    public InvalidPasswordException() {
        super("The given password is invalid.");
    }
}
