package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class InvalidNameException extends SubscriptionException {
    public InvalidNameException() {
        super("The given name is invalid.");
    }
}
