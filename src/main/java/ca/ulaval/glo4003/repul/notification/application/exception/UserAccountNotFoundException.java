package ca.ulaval.glo4003.repul.notification.application.exception;

public class UserAccountNotFoundException extends NotificationException {
    public UserAccountNotFoundException() {
        super("The given account was not found.");
    }
}
