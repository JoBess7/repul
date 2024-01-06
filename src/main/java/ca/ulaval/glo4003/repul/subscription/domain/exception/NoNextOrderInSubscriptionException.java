package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class NoNextOrderInSubscriptionException extends SubscriptionException {
    public NoNextOrderInSubscriptionException() {
        super("There is no next order in this subscription.");
    }
}
