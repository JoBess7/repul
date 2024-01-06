package ca.ulaval.glo4003.repul.subscription.application.exception;

import ca.ulaval.glo4003.repul.subscription.domain.exception.SubscriptionException;

public class CardNumberAlreadyInUseException extends SubscriptionException {
    public CardNumberAlreadyInUseException() {
        super("The card number is already in use.");
    }
}
