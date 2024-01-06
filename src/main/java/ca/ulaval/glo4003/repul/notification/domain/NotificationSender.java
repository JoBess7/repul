package ca.ulaval.glo4003.repul.notification.domain;

public interface NotificationSender {
    void send(Account account, NotificationMessage message);
}
