package ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class InvalidCredentialsException extends UserException {
    public InvalidCredentialsException() {
        super("Invalid email or password.");
    }
}
