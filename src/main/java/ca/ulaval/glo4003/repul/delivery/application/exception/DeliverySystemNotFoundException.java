package ca.ulaval.glo4003.repul.delivery.application.exception;

import ca.ulaval.glo4003.repul.delivery.domain.exception.DeliveryException;

public class DeliverySystemNotFoundException extends DeliveryException {
    public DeliverySystemNotFoundException() {
        super("There is currently no initialized delivery system.");
    }
}
