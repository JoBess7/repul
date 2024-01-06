package ca.ulaval.glo4003.repul.subscription.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.exception.RepULException;

public abstract class SubscriptionException extends RepULException {
    public SubscriptionException(String message) {
        super(message);
    }
}
