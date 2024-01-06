package ca.ulaval.glo4003.repul.delivery.domain.exception;

public class LockerNotAssignedException extends DeliveryException {
    public LockerNotAssignedException() {
        super("There is no locker assigned to this meal kit.");
    }
}
