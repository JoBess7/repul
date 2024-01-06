package ca.ulaval.glo4003.repul.cooking.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.exception.RepULException;

public abstract class CookingException extends RepULException {
    public CookingException(String message) {
        super(message);
    }
}
