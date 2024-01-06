package ca.ulaval.glo4003.repul.small.subscription.application;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.subscription.OrderFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionFixture;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrderPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.application.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private static final DayOfWeek A_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final String A_MEALKIT_TYPE = "STANDARD";
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final SubscriptionQuery A_SUBSCRIPTION_QUERY = new SubscriptionQuery(A_DELIVERY_LOCATION_ID, A_DAY_OF_WEEK, A_MEALKIT_TYPE);
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier ANOTHER_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private SubscriptionService subscriptionService;
    @Mock
    private Subscription mockSubscription;
    @Mock
    private SubscriptionRepository mockSubscriptionRepository;
    @Mock
    private SubscriptionFactory mockSubscriptionFactory;
    @Mock
    private RepULEventBus mockEventBus;
    @Mock
    private PaymentService mockPaymentService;

    @BeforeEach
    public void createSubscriptionService() {
        subscriptionService = new SubscriptionService(mockSubscriptionRepository, mockSubscriptionFactory, mockPaymentService, mockEventBus);
    }

    @Test
    public void whenCreatingSubscription_shouldReturnSubscriptionId() {
        when(mockSubscriptionFactory.createSubscription(any(SubscriberUniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriptionId()).thenReturn(A_SUBSCRIPTION_ID);

        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        assertEquals(A_SUBSCRIPTION_ID, subscriptionId);
    }

    @Test
    public void whenGettingSubscriptions_shouldReturnMatchingSubscriptionsPayload() {
        Subscription subscription = new SubscriptionFixture().build();
        SubscriptionsPayload expectedPayload = new SubscriptionsPayload(List.of(SubscriptionPayload.from(subscription)));
        when(mockSubscriptionRepository.getBySubscriberId(AN_ACCOUNT_ID)).thenReturn(List.of(subscription));

        SubscriptionsPayload subscriptionsPayload = subscriptionService.getSubscriptions(AN_ACCOUNT_ID);

        assertEquals(expectedPayload, subscriptionsPayload);
    }

    @Test
    public void whenGettingSubscriptionById_shouldReturnMatchingSubscriptionsPayload() {
        Subscription subscription = new SubscriptionFixture().withSubscriberId(AN_ACCOUNT_ID).build();
        SubscriptionPayload expectedPayload = SubscriptionPayload.from(subscription);
        when(mockSubscriptionRepository.getById(subscription.getSubscriptionId())).thenReturn(subscription);

        SubscriptionPayload subscriptionPayload = subscriptionService.getSubscriptionById(AN_ACCOUNT_ID, subscription.getSubscriptionId());

        assertEquals(expectedPayload, subscriptionPayload);
    }

    @Test
    public void givenSubscriptionNotForThisSubscriber_whenConfirmingNextMealKitForSubscription_shouldThrow() {
        when(mockSubscriptionFactory.createSubscription(any(SubscriberUniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        when(mockSubscriptionRepository.getById(subscriptionId)).thenReturn(mockSubscription);

        assertThrows(SubscriptionNotFoundException.class, () -> subscriptionService.confirmNextMealKitForSubscription(ANOTHER_ACCOUNT_ID, subscriptionId));
    }

    @Test
    public void givenSubscriptionNotForThisSubscriber_whenDecliningNextMealKitForSubscription_shouldThrow() {
        when(mockSubscriptionFactory.createSubscription(any(SubscriberUniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        when(mockSubscriptionRepository.getById(subscriptionId)).thenReturn(mockSubscription);

        assertThrows(SubscriptionNotFoundException.class, () -> subscriptionService.confirmNextMealKitForSubscription(ANOTHER_ACCOUNT_ID, subscriptionId));
    }

    @Test
    public void whenGettingCurrentOrders_shouldReturnMatchingOrdersPayload() {
        Order order = new OrderFixture().build();
        Subscription subscription = new SubscriptionFixture().addOrder(order).build();
        OrdersPayload expectedPayload = new OrdersPayload(List.of(OrderPayload.from(order)));
        when(mockSubscriptionRepository.getBySubscriberId(AN_ACCOUNT_ID)).thenReturn(List.of(subscription));

        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);

        assertEquals(expectedPayload, ordersPayload);
    }
}
