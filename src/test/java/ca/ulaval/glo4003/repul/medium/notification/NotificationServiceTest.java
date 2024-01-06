package ca.ulaval.glo4003.repul.medium.notification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.notification.application.NotificationService;
import ca.ulaval.glo4003.repul.notification.application.exception.DeliveryPersonAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.application.exception.UserAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationMessage;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryDeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryUserAccountRepository;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.event.UserCreatedEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class NotificationServiceTest {
    private static final SubscriberUniqueIdentifier AN_ACCOUNT_ID = new SubscriberUniqueIdentifier(UUID.randomUUID());
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Name A_NAME = new Name("John Doe");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.now().minusYears(20));
    private static final Gender A_GENDER = Gender.UNDISCLOSED;
    private static final Email AN_EMAIL = new Email("idul12@ulaval.ca");
    private static final Email ANOTHER_EMAIL = new Email("idul456@ulaval.ca");
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new MealKitUniqueIdentifier(UUID.randomUUID());
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new SubscriptionUniqueIdentifier(UUID.randomUUID());
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final LocalDate A_DELIVERY_DATE = LocalDate.now().plusDays(2);
    private static final LocalTime A_DELIVERY_TIME = LocalTime.now().plusHours(48);
    private static final Optional<LockerId> A_LOCKER_ID = Optional.of(new LockerId("LOCKER_ID", 121135));
    private static final CargoUniqueIdentifier A_CARGO_ID = new CargoUniqueIdentifier(UUID.randomUUID());
    private static final KitchenLocationId A_KITCHEN_ID = KitchenLocationId.DESJARDINS;
    private static final DeliveryPersonUniqueIdentifier A_DELIVERY_PERSON_ID = new DeliveryPersonUniqueIdentifier(UUID.randomUUID());
    private static final DeliveryPersonUniqueIdentifier ANOTHER_DELIVERY_PERSON_ID = new DeliveryPersonUniqueIdentifier(UUID.randomUUID());
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto A_KITCHEN_PICKUP_MEAL_KIT_DTO =
        new ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto(A_MEAL_KIT_ID, true);
    private static final ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto A_DELIVERY_PICKUP_MEAL_KIT_DTO =
        new ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto(A_MEAL_KIT_ID, false);    private RepULEventBus eventBus;
    @Mock
    private NotificationSender notificationSender;

    @BeforeEach
    public void createService() {
        eventBus = new GuavaEventBus();
        UserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
        DeliveryPersonAccountRepository deliveryPersonAccountRepository = new InMemoryDeliveryPersonAccountRepository();
        NotificationService notificationService = new NotificationService(userAccountRepository, deliveryPersonAccountRepository, notificationSender);
        eventBus.register(notificationService);
    }

    @Test
    public void givenNoMatchingUserAccountCreated_whenHandlingMealKitConfirmedEvent_shouldThrowDeliveryPersonAccountNotFoundException() {
        assertThrows(UserAccountNotFoundException.class, () -> eventBus.publish(
            new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, AN_ACCOUNT_ID, A_MEAL_KIT_TYPE, Optional.of(A_DELIVERY_LOCATION_ID), A_DELIVERY_DATE)));
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldSendNotificationToUser() {
        eventBus.publish(new UserCreatedEvent(AN_ACCOUNT_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL));
        eventBus.publish(new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, AN_ACCOUNT_ID, A_MEAL_KIT_TYPE,
            Optional.of(A_DELIVERY_LOCATION_ID), A_DELIVERY_DATE));

        eventBus.publish(new ConfirmedDeliveryEvent(A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_DELIVERY_TIME));

        verify(notificationSender).send(any(Account.class), any(NotificationMessage.class));
    }

    @Test
    public void givenNoMatchingMealKitConfirmed_whenHandlingConfirmedDeliveryEvent_shouldThrowDeliveryPersonAccountNotFoundException() {
        eventBus.publish(new UserCreatedEvent(AN_ACCOUNT_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL));

        assertThrows(UserAccountNotFoundException.class,
            () -> eventBus.publish(new ConfirmedDeliveryEvent(A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_DELIVERY_TIME)));
    }

    @Test
    public void whenHandlingMealKitReceivedForDeliveryEvent_shouldSendNotificationToAllAvailableDeliveryPeople() {
        eventBus.publish(new DeliveryPersonAccountCreatedEvent(A_DELIVERY_PERSON_ID, AN_EMAIL));
        eventBus.publish(new DeliveryPersonAccountCreatedEvent(ANOTHER_DELIVERY_PERSON_ID, ANOTHER_EMAIL));

        MealKitDto mealKitDto = new MealKitDto(A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_MEAL_KIT_ID);
        eventBus.publish(
            new MealKitReceivedForDeliveryEvent(A_CARGO_ID, A_KITCHEN_ID, List.of(A_DELIVERY_PERSON_ID, ANOTHER_DELIVERY_PERSON_ID), List.of(mealKitDto)));

        verify(notificationSender, times(2)).send(any(Account.class), any(NotificationMessage.class));
    }

    @Test
    public void givenNoMatchingDeliveryPersonAccountCreated_whenHandlingMealKitReceivedForDeliveryEvent_shouldThrowDeliveryPersonAccountNotFoundException() {
        MealKitDto mealKitDto = new MealKitDto(A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_MEAL_KIT_ID);

        assertThrows(DeliveryPersonAccountNotFoundException.class,
            () -> eventBus.publish(new MealKitReceivedForDeliveryEvent(A_CARGO_ID, A_KITCHEN_ID, List.of(A_DELIVERY_PERSON_ID), List.of(mealKitDto))));
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldCallNotificationSenderForEachInKitchenPickUp() {
        eventBus.publish(new UserCreatedEvent(AN_ACCOUNT_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL));
        eventBus.publish(new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, AN_ACCOUNT_ID,
            A_MEAL_KIT_TYPE, Optional.of(A_DELIVERY_LOCATION_ID), A_DELIVERY_DATE));
        MealKitsCookedEvent mealKitsCookedEvent = new MealKitsCookedEvent(A_KITCHEN_LOCATION_ID.toString(),
            List.of(A_KITCHEN_PICKUP_MEAL_KIT_DTO, A_DELIVERY_PICKUP_MEAL_KIT_DTO));

        eventBus.publish(mealKitsCookedEvent);

        verify(notificationSender, times(1)).send(any(Account.class), any(NotificationMessage.class));
    }
}
