package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class MealKitNotDeliveredException extends DeliveryException {
    public MealKitNotDeliveredException() {
        super("This meal kit has not been delivered.");
    }
}
