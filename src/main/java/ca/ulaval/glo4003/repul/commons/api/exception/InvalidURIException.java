package ca.ulaval.glo4003.repul.commons.api.exception;

import ca.ulaval.glo4003.repul.commons.domain.exception.CommonException;

public class InvalidURIException extends CommonException {
    public InvalidURIException() {
        super("The supplied URI is invalid.");
    }
}
