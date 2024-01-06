package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class SubscriberNotFoundException extends SubscriptionException {
    public SubscriberNotFoundException() {
        super("The given subscriber was not found.");
    }
}
