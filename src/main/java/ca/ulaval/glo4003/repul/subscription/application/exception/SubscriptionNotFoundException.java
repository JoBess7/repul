package ca.ulaval.glo4003.repul.subscription.application.exception;

import ca.ulaval.glo4003.repul.subscription.domain.exception.SubscriptionException;

public class SubscriptionNotFoundException extends SubscriptionException {
    public SubscriptionNotFoundException() {
        super("The given subscription was not found.");
    }
}
