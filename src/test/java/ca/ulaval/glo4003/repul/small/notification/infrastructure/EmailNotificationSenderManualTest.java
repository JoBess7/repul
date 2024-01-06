package ca.ulaval.glo4003.repul.small.notification.infrastructure;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.NotificationMessage;
import ca.ulaval.glo4003.repul.notification.infrastructure.EmailNotificationSender;

public class EmailNotificationSenderManualTest {
    private static final Account AN_ACCOUNT =
        new DeliveryPersonAccount(new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate(), new Email("alexandre.mathieu.7@ulaval.ca"));
    private static final String A_MESSAGE = "An incredibly based email message!";
    private static final String A_TITLE = "An incredibly based email title!";
    private static final NotificationMessage A_NOTIFICATION_MESSAGE = new NotificationMessage(A_TITLE, A_MESSAGE);

    public static void main(String[] args) {
        EmailNotificationSender emailNotificationSender = new EmailNotificationSender();

        emailNotificationSender.send(AN_ACCOUNT, A_NOTIFICATION_MESSAGE);
    }
}
