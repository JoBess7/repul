package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class SemesterNotFoundException extends SubscriptionException {
    public SemesterNotFoundException() {
        super("Could not find the semester matching current date.");
    }
}
