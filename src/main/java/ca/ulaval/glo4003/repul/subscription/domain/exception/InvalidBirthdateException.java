package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class InvalidBirthdateException extends SubscriptionException {
    public InvalidBirthdateException() {
        super("The given birthdate is invalid.");
    }
}
