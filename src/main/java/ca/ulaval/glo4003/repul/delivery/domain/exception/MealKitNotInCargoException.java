package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class MealKitNotInCargoException extends DeliveryException {
    public MealKitNotInCargoException() {
        super("The specified meal kit is not in the cargo.");
    }
}
