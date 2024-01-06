package ca.ulaval.glo4003.repul.medium.notification.infrastructure;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.notification.application.NotificationService;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.EmailNotificationSender;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryDeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryUserAccountRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceWithEmailManualTest {
    private static final DeliveryPersonUniqueIdentifier A_VALID_DELIVERY_ACCOUNT_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final DeliveryPersonUniqueIdentifier ANOTHER_VALID_DELIVERY_ACCOUNT_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final Email AN_EMAIL = new Email("alexandre.mathieu.7@ulaval.ca");
    private static final DeliveryPersonAccount A_DELIVERY_ACCOUNT = new DeliveryPersonAccount(A_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final DeliveryPersonAccount ANOTHER_DELIVERY_ACCOUNT = new DeliveryPersonAccount(ANOTHER_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final Optional<LockerId> A_LOCKER_ID = Optional.of(new LockerId("A_LOCKER_ID", 1));
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final DeliveryLocationId ANOTHER_DELIVERY_LOCATION_ID = DeliveryLocationId.PEPS;
    private static final Optional<LockerId> ANOTHER_LOCKER_ID = Optional.of(new LockerId("ANOTHER_LOCKER_ID", 2));
    private static final MealKitUniqueIdentifier ANOTHER_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final CargoUniqueIdentifier A_CARGO_ID = new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generate();

    private static final List<DeliveryPersonUniqueIdentifier> AVAILABLE_SHIPPERS_IDS = List.of(A_VALID_DELIVERY_ACCOUNT_ID, ANOTHER_VALID_DELIVERY_ACCOUNT_ID);
    private static final List<MealKitDto> MEAL_KIT_DTOS = List.of(new MealKitDto(A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_MEAL_KIT_ID),
        new MealKitDto(ANOTHER_DELIVERY_LOCATION_ID, ANOTHER_LOCKER_ID, ANOTHER_MEAL_KIT_ID));
    private static final MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent =
        new MealKitReceivedForDeliveryEvent(A_CARGO_ID, A_KITCHEN_LOCATION_ID, AVAILABLE_SHIPPERS_IDS, MEAL_KIT_DTOS);

    public static void main(String[] args) {
        NotificationSender notificationSender = new EmailNotificationSender();
        DeliveryPersonAccountRepository deliveryDeliveryPersonAccountRepository = new InMemoryDeliveryPersonAccountRepository();
        UserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
        NotificationService notificationService = new NotificationService(userAccountRepository, deliveryDeliveryPersonAccountRepository, notificationSender);

        deliveryDeliveryPersonAccountRepository.save(A_DELIVERY_ACCOUNT);
        deliveryDeliveryPersonAccountRepository.save(ANOTHER_DELIVERY_ACCOUNT);

        notificationService.handleMealKitReceivedForDeliveryEvent(mealKitReceivedForDeliveryEvent);
    }
}
