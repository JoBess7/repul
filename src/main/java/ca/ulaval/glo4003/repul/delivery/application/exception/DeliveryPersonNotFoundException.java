package ca.ulaval.glo4003.repul.delivery.application.exception;

import ca.ulaval.glo4003.repul.delivery.domain.exception.DeliveryException;

public class DeliveryPersonNotFoundException extends DeliveryException {
    public DeliveryPersonNotFoundException() {
        super("The given delivery person was not found.");
    }
}
