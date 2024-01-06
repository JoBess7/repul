package ca.ulaval.glo4003.repul.small.notification.application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.notification.application.NotificationService;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationMessage;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.domain.UserAccount;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.event.UserCreatedEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    private static final SubscriberUniqueIdentifier A_VALID_SUBSCRIBER_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier A_VALID_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final DeliveryPersonUniqueIdentifier A_VALID_DELIVERY_ACCOUNT_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final DeliveryPersonUniqueIdentifier ANOTHER_VALID_DELIVERY_ACCOUNT_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final DeliveryPersonUniqueIdentifier AN_INVALID_DELIVERY_ACCOUNT_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Name A_NAME = new Name("John Doe");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.now().minusYears(20));
    private static final Gender A_GENDER = Gender.UNDISCLOSED;
    private static final Email AN_EMAIL = new Email("alexandre.mathieu.7@ulaval.ca");
    private static final DeliveryPersonAccount A_DELIVERY_ACCOUNT = new DeliveryPersonAccount(A_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final DeliveryPersonAccount ANOTHER_DELIVERY_ACCOUNT = new DeliveryPersonAccount(ANOTHER_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final UserAccount A_VALID_USER_ACCOUNT = new UserAccount(A_VALID_SUBSCRIBER_ACCOUNT_ID, AN_EMAIL);
    private static final LocalTime A_TIME = LocalTime.now();
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final Optional<LockerId> A_LOCKER_ID = Optional.of(new LockerId("A_LOCKER_ID", 1));
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final DeliveryLocationId ANOTHER_DELIVERY_LOCATION_ID = DeliveryLocationId.PEPS;
    private static final Optional<LockerId> ANOTHER_LOCKER_ID = Optional.of(new LockerId("ANOTHER_LOCKER_ID", 2));
    private static final MealKitUniqueIdentifier ANOTHER_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final CargoUniqueIdentifier A_CARGO_ID = new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generate();
    private static final List<DeliveryPersonUniqueIdentifier> AVAILABLE_DELIVERY_PEOPLE_IDS =
        List.of(A_VALID_DELIVERY_ACCOUNT_ID, ANOTHER_VALID_DELIVERY_ACCOUNT_ID);
    private static final List<DeliveryPersonUniqueIdentifier> AVAILABLE_DELIVERY_PEOPLE_IDS_WITH_INVALID_ACCOUNT =
        List.of(A_VALID_DELIVERY_ACCOUNT_ID, AN_INVALID_DELIVERY_ACCOUNT_ID);
    private static final List<MealKitDto> MEAL_KIT_DTOS = List.of(new MealKitDto(A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_MEAL_KIT_ID),
        new MealKitDto(ANOTHER_DELIVERY_LOCATION_ID, ANOTHER_LOCKER_ID, ANOTHER_MEAL_KIT_ID));
    private static final MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent =
        new MealKitReceivedForDeliveryEvent(A_CARGO_ID, A_KITCHEN_LOCATION_ID, AVAILABLE_DELIVERY_PEOPLE_IDS, MEAL_KIT_DTOS);
    private static final MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEventWithInvalidDeliveryAccount =
        new MealKitReceivedForDeliveryEvent(A_CARGO_ID, A_KITCHEN_LOCATION_ID, AVAILABLE_DELIVERY_PEOPLE_IDS_WITH_INVALID_ACCOUNT, MEAL_KIT_DTOS);
    private static final ConfirmedDeliveryEvent MEAL_KIT_DELIVERED_EVENT =
        new ConfirmedDeliveryEvent(A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_TIME);
    private static final MealKitConfirmedEvent A_MEAL_KIT_CONFIRMED_EVENT = new MealKitConfirmedEvent(A_MEAL_KIT_ID,
        A_VALID_SUBSCRIPTION_ID, A_VALID_SUBSCRIBER_ACCOUNT_ID, MealKitType.STANDARD, Optional.of(A_DELIVERY_LOCATION_ID), LocalDate.now());
    private static final UserCreatedEvent AN_ACCOUNT_CREATED_EVENT =
        new UserCreatedEvent(A_VALID_SUBSCRIBER_ACCOUNT_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);
    private static final DeliveryPersonAccountCreatedEvent A_DELIVERY_PERSON_ACCOUNT_CREATED_EVENT =
        new DeliveryPersonAccountCreatedEvent(A_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto A_KITCHEN_PICKUP_MEAL_KIT_DTO =
        new ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto(A_MEAL_KIT_ID, true);
    private static final ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto A_DELIVERY_PICKUP_MEAL_KIT_DTO =
        new ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto(A_MEAL_KIT_ID, false);
    private NotificationService notificationService;

    @Mock
    private DeliveryPersonAccountRepository deliveryPersonAccountRepository;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private NotificationSender notificationSender;

    @BeforeEach
    public void createService() {
        notificationService = new NotificationService(userAccountRepository, deliveryPersonAccountRepository, notificationSender);
    }

    @Test
    public void whenHandlingUserAccountCreation_shouldSaveUserAccountRepository() {
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(A_VALID_SUBSCRIBER_ACCOUNT_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);

        notificationService.handleUserCreated(userCreatedEvent);

        verify(userAccountRepository, times(1)).save(any(UserAccount.class));
    }

    @Test
    public void whenHandlingAccountDeliveryCreation_shouldSaveDeliveryAccountRepository() {
        DeliveryPersonAccountCreatedEvent deliveryPersonAccountCreatedEvent = new DeliveryPersonAccountCreatedEvent(A_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);

        notificationService.handleDeliveryAccountCreated(deliveryPersonAccountCreatedEvent);

        verify(deliveryPersonAccountRepository, times(1)).save(any(DeliveryPersonAccount.class));
    }

    @Test
    public void whenHandlingMealKitReceivedForDeliveryEvent_shouldCallNotificationSenderGoodAmountOfTimes() {
        when(deliveryPersonAccountRepository.getByAccountId(any())).thenReturn(A_DELIVERY_ACCOUNT);

        notificationService.handleMealKitReceivedForDeliveryEvent(mealKitReceivedForDeliveryEvent);

        verify(notificationSender, times(AVAILABLE_DELIVERY_PEOPLE_IDS.size())).send(any(Account.class), any(NotificationMessage.class));
    }

    @Test
    public void whenHandlingMealKitReceivedForDeliveryEvent_shouldCallNotificationSenderWithRightAccountAndAMessage() {
        when(deliveryPersonAccountRepository.getByAccountId(A_VALID_DELIVERY_ACCOUNT_ID)).thenReturn(A_DELIVERY_ACCOUNT);
        when(deliveryPersonAccountRepository.getByAccountId(ANOTHER_VALID_DELIVERY_ACCOUNT_ID)).thenReturn(ANOTHER_DELIVERY_ACCOUNT);

        notificationService.handleMealKitReceivedForDeliveryEvent(mealKitReceivedForDeliveryEvent);

        verify(notificationSender, times(1)).send(eq(A_DELIVERY_ACCOUNT), any(NotificationMessage.class));
        verify(notificationSender, times(1)).send(eq(ANOTHER_DELIVERY_ACCOUNT), any(NotificationMessage.class));
    }

    @Test
    public void whenHandlingMealKitDeliveredEvent_shouldCallNotificationSenderWithRightAccountAndAMessage() {
        when(userAccountRepository.getAccountByMealKitId(A_MEAL_KIT_ID)).thenReturn(A_VALID_USER_ACCOUNT);

        notificationService.handleConfirmedDeliveryEvent(MEAL_KIT_DELIVERED_EVENT);

        verify(notificationSender, times(1)).send(eq(A_VALID_USER_ACCOUNT), any(NotificationMessage.class));
    }

    @Test
    public void whenHandleMealKitDeliveredEvent_shouldCallNotificationSenderWithGoodAccount() {
        when(userAccountRepository.getAccountByMealKitId(A_MEAL_KIT_ID)).thenReturn(A_VALID_USER_ACCOUNT);

        notificationService.handleConfirmedDeliveryEvent(MEAL_KIT_DELIVERED_EVENT);

        verify(notificationSender, times(1)).send(eq(A_VALID_USER_ACCOUNT), any(NotificationMessage.class));
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_ShouldAddMealKitToUser() {
        when(userAccountRepository.getAccountById(A_VALID_SUBSCRIBER_ACCOUNT_ID)).thenReturn(A_VALID_USER_ACCOUNT);
        int numberOfMealKits = A_VALID_USER_ACCOUNT.getMealKitIds().size();

        notificationService.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        assertEquals(A_VALID_USER_ACCOUNT.getMealKitIds().size(), numberOfMealKits + 1);
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldSaveMealKitInUserAccountRepository() {
        when(userAccountRepository.getAccountById(A_VALID_SUBSCRIBER_ACCOUNT_ID)).thenReturn(A_VALID_USER_ACCOUNT);

        notificationService.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(userAccountRepository).save(any(UserAccount.class));
    }

    @Test
    public void whenHandlingUserAccountCreated_shouldSaveInUserAccountRepository() {
        notificationService.handleUserCreated(AN_ACCOUNT_CREATED_EVENT);

        verify(userAccountRepository).save(any(UserAccount.class));
    }

    @Test
    public void whenHandlingDeliveryPersonAccountCreated_shouldSaveInDeliveryPersonAccountRepository() {
        notificationService.handleDeliveryAccountCreated(A_DELIVERY_PERSON_ACCOUNT_CREATED_EVENT);

        verify(deliveryPersonAccountRepository).save(any(DeliveryPersonAccount.class));
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldCallNotificationSenderForEachInKitchenPickUp() {
        when(userAccountRepository.getAccountByMealKitId(A_MEAL_KIT_ID)).thenReturn(A_VALID_USER_ACCOUNT);
        MealKitsCookedEvent mealKitsCookedEvent = new MealKitsCookedEvent(A_KITCHEN_LOCATION_ID.toString(),
            List.of(A_KITCHEN_PICKUP_MEAL_KIT_DTO, A_DELIVERY_PICKUP_MEAL_KIT_DTO));

        notificationService.handleMealKitsCookedEvent(mealKitsCookedEvent);

        verify(notificationSender, times(1)).send(any(Account.class), any(NotificationMessage.class));
    }
}
