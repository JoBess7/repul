package ca.ulaval.glo4003.repul.config.initializer;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.notification.application.NotificationService;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.domain.UserAccount;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryDeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryUserAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.LogNotificationSender;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

public class NotificationContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationContextInitializer.class);

    private UserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
    private DeliveryPersonAccountRepository deliveryPersonAccountRepository = new InMemoryDeliveryPersonAccountRepository();
    private NotificationSender notificationSender = new LogNotificationSender();

    public NotificationContextInitializer withEmptyDeliveryAccountRepository(
        DeliveryPersonAccountRepository deliveryDeliveryPersonAccountRepository) {
        this.deliveryPersonAccountRepository = deliveryDeliveryPersonAccountRepository;
        return this;
    }

    public NotificationContextInitializer withEmptyUserAccountRepository(
        UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        return this;
    }

    public NotificationContextInitializer withNotificationSender(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
        return this;
    }

    public NotificationContextInitializer withDeliveryAccounts(List<Map<DeliveryPersonUniqueIdentifier, String>> accounts) {
        accounts.forEach(
            account -> account.forEach((id, email) -> deliveryPersonAccountRepository.save(new DeliveryPersonAccount(id, new Email(email)))));
        return this;
    }

    public NotificationContextInitializer withUserAccounts(List<Map<SubscriberUniqueIdentifier, String>> accounts) {
        accounts.forEach(account -> account.forEach((id, email) -> userAccountRepository.save(new UserAccount(id, new Email(email)))));
        return this;
    }

    public NotificationContextInitializer withConfirmedSubscriptions(List<Subscription> subscriptions) {
        subscriptions.forEach(subscription -> {
            UserAccount userAccount = userAccountRepository.getAccountById(subscription.getSubscriberId());
            Order order = subscription.findCurrentOrder().orElseThrow(() -> new RuntimeException("Subscription must have a current order."));
            userAccount.addMealKit(order.getOrderId());
            userAccountRepository.save(userAccount);
        });
        return this;
    }

    public NotificationContextInitializer withMealKitIdForUser(MealKitUniqueIdentifier mealKitId, SubscriberUniqueIdentifier accountId) {
        UserAccount userAccount = userAccountRepository.getAccountById(accountId);
        userAccount.addMealKit(mealKitId);
        userAccountRepository.save(userAccount);
        return this;
    }

    public NotificationService createNotificationService(RepULEventBus eventBus) {
        LOGGER.info("Creating Notification service");
        NotificationService notificationService = new NotificationService(userAccountRepository, deliveryPersonAccountRepository, notificationSender);
        eventBus.register(notificationService);
        return notificationService;
    }
}
