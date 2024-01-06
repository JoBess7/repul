package ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class IDULAlreadyInUseException extends UserException {
    public IDULAlreadyInUseException() {
        super("An account with this IDUL already exists.");
    }
}
