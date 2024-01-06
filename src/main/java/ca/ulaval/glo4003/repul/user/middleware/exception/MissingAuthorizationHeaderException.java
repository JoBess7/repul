package ca.ulaval.glo4003.repul.user.middleware.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class MissingAuthorizationHeaderException extends UserException {
    public MissingAuthorizationHeaderException() {
        super("Missing Bearer token in Authorization header.");
    }
}
