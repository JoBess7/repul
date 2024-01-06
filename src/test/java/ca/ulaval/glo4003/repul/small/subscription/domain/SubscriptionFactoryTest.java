package ca.ulaval.glo4003.repul.small.subscription.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SemesterNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrdersFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionFactoryTest {
    private static final SubscriptionUniqueIdentifier A_UNIQUE_SUBSCRIPTION_IDENTIFIER = new SubscriptionUniqueIdentifier(UUID.randomUUID());
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new SubscriberUniqueIdentifier(UUID.randomUUID());
    private static final Semester CURRENT_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now().minusDays(60), LocalDate.now().plusDays(17));
    private static final DeliveryLocationId A_VALID_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final DeliveryLocationId ANOTHER_VALID_DELIVERY_LOCATION_ID = DeliveryLocationId.PEPS;
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final DayOfWeek A_WEEKDAY = DayOfWeek.MONDAY;
    private static final Order AN_ORDER = new Order(new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate(), A_MEALKIT_TYPE, LocalDate.now());

    @Mock
    private OrdersFactory ordersFactory;
    @Mock
    private UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory;
    @Mock
    private UniqueIdentifierFactory<SubscriptionUniqueIdentifier> subscriptionUniqueIdentifierFactory;

    private SubscriptionFactory subscriptionFactory;

    @BeforeEach
    public void createSubscriptionFactory() {
        subscriptionFactory =
            new SubscriptionFactory(mealKitUniqueIdentifierFactory, subscriptionUniqueIdentifierFactory, ordersFactory, List.of(CURRENT_SEMESTER),
                List.of(A_VALID_DELIVERY_LOCATION_ID, ANOTHER_VALID_DELIVERY_LOCATION_ID));
    }

    @Test
    public void givenValidParameters_whenCreatingSubscription_shouldCreateSubscription() {
        when(subscriptionUniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_SUBSCRIPTION_IDENTIFIER);
        when(ordersFactory.createOrdersInSemester(any(), any(), any(), any())).thenReturn(List.of(AN_ORDER));
        DayOfWeek chosenDayOfWeek = DayOfWeek.from(LocalDate.now().plusDays(3));

        Subscription subscription = subscriptionFactory.createSubscription(A_SUBSCRIBER_ID, A_VALID_DELIVERY_LOCATION_ID, chosenDayOfWeek, A_MEALKIT_TYPE);

        assertEquals(A_UNIQUE_SUBSCRIPTION_IDENTIFIER, subscription.getSubscriptionId());
        assertEquals(A_SUBSCRIBER_ID, subscription.getSubscriberId());
        assertEquals(DayOfWeek.from(chosenDayOfWeek), subscription.getFrequency().dayOfWeek());
        assertEquals(A_VALID_DELIVERY_LOCATION_ID, subscription.getDeliveryLocationId());
        assertEquals(LocalDate.now(), subscription.getStartDate());
        assertEquals(CURRENT_SEMESTER, subscription.getSemester());
        Assertions.assertEquals(A_MEALKIT_TYPE, subscription.getMealKitType());
        assertFalse(subscription.getOrders().isEmpty());
    }

    @Test
    public void givenUnsupportedLocationId_whenCreatingSubscription_shouldThrowInvalidLocationException() {
        DeliveryLocationId unsupportedDeliveryLocationId = DeliveryLocationId.VACHON;
        subscriptionFactory =
            new SubscriptionFactory(mealKitUniqueIdentifierFactory, subscriptionUniqueIdentifierFactory, ordersFactory, List.of(CURRENT_SEMESTER), List.of());

        assertThrows(InvalidLocationIdException.class,
            () -> subscriptionFactory.createSubscription(A_SUBSCRIBER_ID, unsupportedDeliveryLocationId, A_WEEKDAY, A_MEALKIT_TYPE));
    }

    @Test
    public void givenCurrentDateNotInSupportedSemesters_whenCreatingSubscription_shouldThrowSemesterNotFoundException() {
        Semester alreadyEndedSemester = new Semester(new SemesterCode("A23"), LocalDate.now().minusDays(90), LocalDate.now().minusDays(3));
        subscriptionFactory =
            new SubscriptionFactory(mealKitUniqueIdentifierFactory, subscriptionUniqueIdentifierFactory, ordersFactory, List.of(alreadyEndedSemester),
                List.of(A_VALID_DELIVERY_LOCATION_ID));

        assertThrows(SemesterNotFoundException.class,
            () -> subscriptionFactory.createSubscription(A_SUBSCRIBER_ID, A_VALID_DELIVERY_LOCATION_ID, A_WEEKDAY, A_MEALKIT_TYPE));
    }
}
