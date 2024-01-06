package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class InvalidSemesterCodeException extends SubscriptionException {
    public InvalidSemesterCodeException() {
        super("The given semester code is invalid.");
    }
}
