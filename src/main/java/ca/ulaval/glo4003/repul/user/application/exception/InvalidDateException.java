package ca.ulaval.glo4003.repul.user.application.exception;

import ca.ulaval.glo4003.repul.user.domain.exception.UserException;

public class InvalidDateException extends UserException {
    public InvalidDateException() {
        super("The given date is in an invalid date format. Use yyyy-MM-dd.");
    }
}
