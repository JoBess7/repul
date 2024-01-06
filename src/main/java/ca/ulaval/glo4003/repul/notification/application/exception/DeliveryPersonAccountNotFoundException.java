package ca.ulaval.glo4003.repul.notification.application.exception;

public class DeliveryPersonAccountNotFoundException extends NotificationException {
    public DeliveryPersonAccountNotFoundException() {
        super("The given account was not found.");
    }
}
