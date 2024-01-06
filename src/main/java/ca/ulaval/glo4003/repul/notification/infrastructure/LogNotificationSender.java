package ca.ulaval.glo4003.repul.notification.infrastructure;

import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.NotificationMessage;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;

public class LogNotificationSender implements NotificationSender {
    @Override
    public void send(Account account, NotificationMessage message) {
        System.out.println("Sending notification to: " + account.getEmail().value() + " with message: [" + message.title() + "] " + message.body());
    }
}
