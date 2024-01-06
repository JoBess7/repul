package ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class PasswordNotMatchingException extends UserException {
    public PasswordNotMatchingException() {
        super("Provided password does not match the user's password.");
    }
}
