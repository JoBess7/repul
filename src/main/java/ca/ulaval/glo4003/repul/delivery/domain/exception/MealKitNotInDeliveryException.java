package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class MealKitNotInDeliveryException extends DeliveryException {
    public MealKitNotInDeliveryException() {
        super("This meal kit is not in delivery.");
    }
}
