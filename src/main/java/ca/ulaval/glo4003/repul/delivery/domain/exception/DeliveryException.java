package ca.ulaval.glo4003.repul.delivery.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.exception.RepULException;

public abstract class DeliveryException extends RepULException {
    public DeliveryException(String message) {
        super(message);
    }
}
