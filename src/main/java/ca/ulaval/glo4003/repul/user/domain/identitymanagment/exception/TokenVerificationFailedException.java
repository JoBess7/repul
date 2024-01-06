package ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class TokenVerificationFailedException extends UserException {
    public TokenVerificationFailedException() {
        super("Invalid or expired token.");
    }
}
