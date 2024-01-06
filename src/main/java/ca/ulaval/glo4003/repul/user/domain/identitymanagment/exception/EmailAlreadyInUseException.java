package ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class EmailAlreadyInUseException extends UserException {
    public EmailAlreadyInUseException() {
        super("An account with this email address already exists.");
    }
}
