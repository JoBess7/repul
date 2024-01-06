package ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class UserNotFoundException extends UserException {
    public UserNotFoundException() {
        super("Could not find a user with this email.");
    }
}
