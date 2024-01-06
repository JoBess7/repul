package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class OrderCannotBeDeclinedException extends SubscriptionException {
    public OrderCannotBeDeclinedException() {
        super("This order cannot be declined.");
    }
}
