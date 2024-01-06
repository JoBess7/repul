package ca.ulaval.glo4003.repul.subscription.domain.exception;

public class OrderCannotBeConfirmedException extends SubscriptionException {
    public OrderCannotBeConfirmedException() {
        super("This order cannot be confirmed.");
    }
}
