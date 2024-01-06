package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class NoOrdersInDesiredPeriodException extends SubscriptionException {
    public NoOrdersInDesiredPeriodException() {
        super("There would be no mealkits in the desired period.");
    }
}
