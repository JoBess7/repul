package ca.ulaval.glo4003.repul.small.lockerauthorization.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.lockerauthorization.api.LockerAuthorizationEventHandler;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.UserCardAddedEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LockerAuthorizationEventHandlerTest {
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.DESJARDINS;
    private static final LocalDate A_DATE = LocalDate.now();
    private static final LocalTime A_TIME = LocalTime.now();
    private static final UserCardNumber A_USER_CARD_NUMBER = new UserCardNumber("999999999");
    private static final LockerId A_LOCKER_ID = new LockerId("Id", 2);
    private static final ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId A_LOCKER_AUTH_LOCKER_ID =
        new ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId("Id");
    private static final MealKitConfirmedEvent A_MEAL_KIT_CONFIRMED_EVENT = new MealKitConfirmedEvent(A_MEAL_KIT_ID,
        A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID, A_MEAL_KIT_TYPE, Optional.of(A_DELIVERY_LOCATION_ID), A_DATE);
    private static final MealKitConfirmedEvent A_MEAL_KIT_CONFIRMED_EVENT_WITHOUT_DELIVERY_LOCATION = new MealKitConfirmedEvent(
        A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID, A_MEAL_KIT_TYPE, Optional.empty(), A_DATE);
    private static final ConfirmedDeliveryEvent A_CONFIRMED_DELIVERY_EVENT = new
        ConfirmedDeliveryEvent(A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID, Optional.of(A_LOCKER_ID), A_TIME);
    private static final RecalledDeliveryEvent A_RECALLED_DELIVERY_EVENT =
        new RecalledDeliveryEvent(A_MEAL_KIT_ID, A_LOCKER_ID, A_DELIVERY_LOCATION_ID);
    private static final UserCardAddedEvent A_USER_CARD_ADDED_EVENT = new UserCardAddedEvent(A_SUBSCRIBER_ID, A_USER_CARD_NUMBER);

    private LockerAuthorizationEventHandler lockerAuthorizationEventHandler;

    @Mock
    private LockerAuthorizationService lockerAuthorizationService;

    @BeforeEach
    public void createLockerAuthorizationEventHandler() {
        lockerAuthorizationEventHandler = new LockerAuthorizationEventHandler(lockerAuthorizationService);
    }

    @Test
    public void givenADeliveryLocationIdInEvent_whenHandlingMealKitConfirmedEvent_shouldCallCreateOrderInService() {
        lockerAuthorizationEventHandler.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(lockerAuthorizationService, times(1)).createOrder(A_SUBSCRIBER_ID, A_MEAL_KIT_ID);
    }

    @Test
    public void givenNoDeliveryLocationIdInEvent_whenHandlingMealKitConfirmedEvent_shouldNotCallService() {
        lockerAuthorizationEventHandler.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT_WITHOUT_DELIVERY_LOCATION);

        verify(lockerAuthorizationService, times(0)).createOrder(A_SUBSCRIBER_ID, A_MEAL_KIT_ID);
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldCallAssignLockerInService() {
        lockerAuthorizationEventHandler.handleConfirmedDeliveryEvent(A_CONFIRMED_DELIVERY_EVENT);

        verify(lockerAuthorizationService, times(1)).assignLockerToMealKit(A_MEAL_KIT_ID, A_LOCKER_AUTH_LOCKER_ID);
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldCallRecallMealKitInService() {
        lockerAuthorizationEventHandler.handleRecalledDeliveryEvent(A_RECALLED_DELIVERY_EVENT);

        verify(lockerAuthorizationService, times(1)).unassignLocker(A_MEAL_KIT_ID);
    }

    @Test
    public void whenHandlingUserCardAddedEvent_shouldCallRegisterUserCardNumberKitInService() {
        lockerAuthorizationEventHandler.handleUserCardAddedEvent(A_USER_CARD_ADDED_EVENT);

        verify(lockerAuthorizationService, times(1)).registerSubscriberCardNumber(A_SUBSCRIBER_ID, A_USER_CARD_NUMBER);
    }
}
