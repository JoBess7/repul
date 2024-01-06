package ca.ulaval.glo4003.repul.medium.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.CanceledCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.fixture.subscription.OrderFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionFixture;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrderPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.application.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrdersFactory;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.infrastructure.LogPaymentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SubscriptionServiceTest {
    private static final DeliveryLocationId A_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final DeliveryLocationId ANOTHER_LOCATION_ID = DeliveryLocationId.PEPS;
    private static final DayOfWeek A_DAY_STRING = DayOfWeek.from(LocalDate.now().plusDays(3));
    private static final String A_MEALKIT_TYPE = "STANDARD";
    private static final SubscriptionQuery A_SUBSCRIPTION_QUERY = new SubscriptionQuery(A_LOCATION_ID, A_DAY_STRING, A_MEALKIT_TYPE);
    private static final SubscriptionQuery ANOTHER_SUBSCRIPTION_QUERY = new SubscriptionQuery(ANOTHER_LOCATION_ID, A_DAY_STRING, A_MEALKIT_TYPE);
    private static final SubscriberUniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier ANOTHER_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final Semester CURRENT_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2));
    private static final LockerId A_LOCKER_ID = new LockerId("123", 4);
    private RepULEventBus eventBus;
    private PaymentService paymentService;
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void createService() {
        eventBus = new GuavaEventBus();
        paymentService = new LogPaymentService();
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();
        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory =
            new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        SubscriptionFactory subscriptionFactory =
            new SubscriptionFactory(mealKitUniqueIdentifierFactory, new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class),
                new OrdersFactory(mealKitUniqueIdentifierFactory), List.of(CURRENT_SEMESTER), List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));
        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionFactory, paymentService, eventBus);
        eventBus.register(subscriptionService);
    }

    @Test
    public void whenCreatingSubscription_shouldPersistSubscription() {
        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        SubscriptionsPayload persistedSubscriptions = subscriptionService.getSubscriptions(AN_ACCOUNT_ID);
        assertEquals(subscriptionId, persistedSubscriptions.subscriptions().get(0).subscriptionId());
    }

    @Test
    public void givenCreatedSubscriptions_whenGettingSubscriptions_shouldReturnSubscriptionsInAccount() {
        UniqueIdentifier firstSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        UniqueIdentifier secondSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, ANOTHER_SUBSCRIPTION_QUERY);
        subscriptionService.createSubscription(ANOTHER_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        SubscriptionsPayload subscriptions = subscriptionService.getSubscriptions(AN_ACCOUNT_ID);

        assertEquals(2, subscriptions.subscriptions().size());

        List<UniqueIdentifier> expectedSubscriptionIds = List.of(firstSubscriptionId, secondSubscriptionId);
        assertTrue(expectedSubscriptionIds.contains(subscriptions.subscriptions().get(0).subscriptionId()));
        assertTrue(expectedSubscriptionIds.contains(subscriptions.subscriptions().get(1).subscriptionId()));
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldMarkMatchingOrdersAsToDeliver() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        SubscriptionUniqueIdentifier otherSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, ANOTHER_SUBSCRIPTION_QUERY);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, otherSubscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        List<MealKitUniqueIdentifier> orderIds = ordersPayload.orders().stream().map(OrderPayload::orderId).toList();
        List<MealKitDto> mealKitDtos = orderIds.stream().map(orderId -> new MealKitDto(orderId, true)).toList();
        MealKitsCookedEvent event = new MealKitsCookedEvent(A_LOCATION_ID.toString(), mealKitDtos);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.TO_DELIVER, updatedOrdersPayload.orders().get(0).orderStatus());
        assertEquals(OrderStatus.TO_DELIVER, updatedOrdersPayload.orders().get(1).orderStatus());
    }

    @Test
    public void whenHandlingPickedUpCargoEvent_shouldMarkMatchingOrdersAsInDelivery() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        SubscriptionUniqueIdentifier otherSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, ANOTHER_SUBSCRIPTION_QUERY);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, otherSubscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        List<MealKitUniqueIdentifier> orderIds = ordersPayload.orders().stream().map(OrderPayload::orderId).toList();
        PickedUpCargoEvent event = new PickedUpCargoEvent(orderIds);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.IN_DELIVERY, updatedOrdersPayload.orders().get(0).orderStatus());
        assertEquals(OrderStatus.IN_DELIVERY, updatedOrdersPayload.orders().get(1).orderStatus());
    }

    @Test
    public void whenHandlingCanceledCargoEvent_shouldMarkMatchingOrdersAsToDeliver() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        SubscriptionUniqueIdentifier otherSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, ANOTHER_SUBSCRIPTION_QUERY);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, otherSubscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        List<MealKitUniqueIdentifier> orderIds = ordersPayload.orders().stream().map(OrderPayload::orderId).toList();
        CanceledCargoEvent event = new CanceledCargoEvent(orderIds);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.TO_DELIVER, updatedOrdersPayload.orders().get(0).orderStatus());
        assertEquals(OrderStatus.TO_DELIVER, updatedOrdersPayload.orders().get(1).orderStatus());
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldMarkMatchingOrdersAsToPickup() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        MealKitUniqueIdentifier orderId = ordersPayload.orders().stream().map(OrderPayload::orderId).toList().get(0);
        ConfirmedDeliveryEvent event = new ConfirmedDeliveryEvent(orderId, A_LOCATION_ID, Optional.of(A_LOCKER_ID), LocalTime.now());

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.TO_PICKUP, updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldMarkMatchingOrdersAsInDelivery() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        MealKitUniqueIdentifier orderId = ordersPayload.orders().stream().map(OrderPayload::orderId).toList().get(0);
        RecalledDeliveryEvent event = new RecalledDeliveryEvent(orderId, A_LOCKER_ID, A_LOCATION_ID);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.IN_DELIVERY, updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void whenConfirmingNextMealKitForSubscription_shouldUpdateMatchingOrderInSubscription() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.CONFIRMED, updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void whenDecliningNextMealKitForSubscription_shouldUpdateMatchingOrderInSubscription() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        subscriptionService.declineNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.DECLINED, updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void whenGettingCurrentOrders_shouldReturnCurrentOrdersForSubscriptionsInAccount() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        subscriptionService.createSubscription(AN_ACCOUNT_ID, ANOTHER_SUBSCRIPTION_QUERY);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        OrdersPayload currentOrders = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);

        assertEquals(2, currentOrders.orders().size());
        assertTrue(currentOrders.orders().stream().anyMatch(order -> order.orderStatus().equals(OrderStatus.CONFIRMED)));
        assertTrue(currentOrders.orders().stream().anyMatch(order -> order.orderStatus().equals(OrderStatus.PENDING)));
    }

    @Test
    public void whenHandlingMealKitPickedUpByUserEvent_shouldMarkMatchingOrderAsPickedUp() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        MealKitUniqueIdentifier orderId = ordersPayload.orders().stream().map(OrderPayload::orderId).toList().get(0);
        MealKitPickedUpByUserEvent event = new MealKitPickedUpByUserEvent(orderId);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.PICKED_UP, updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void whenProcessingConfirmedAndReadyToProcessOrders_shouldMarkThemAsToCook() {
        Order order = givenAConfirmedOrderReadyToBeProcessed();

        subscriptionService.processConfirmationForTheDay();

        assertEquals(OrderStatus.TO_COOK, order.getOrderStatus());
    }

    private Order givenAConfirmedOrderReadyToBeProcessed() {
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();
        SubscriptionFixture subscriptionFixture = new SubscriptionFixture();
        OrderFixture orderFixture = new OrderFixture();
        Order order = orderFixture.withOrderStatus(OrderStatus.CONFIRMED).withDeliveryDate(LocalDate.now().plusDays(1)).build();
        Subscription subscription = subscriptionFixture.withOrders(List.of(order)).build();
        subscriptionRepository.save(subscription);
        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory =
            new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        SubscriptionFactory subscriptionFactory =
            new SubscriptionFactory(mealKitUniqueIdentifierFactory, new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class),
                new OrdersFactory(mealKitUniqueIdentifierFactory), List.of(CURRENT_SEMESTER), List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));
        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionFactory, paymentService, eventBus);
        eventBus.register(subscriptionService);

        return order;
    }

    @Test
    public void whenProcessingPendingAndReadyToProcessOrders_shouldMarkThemAsDeclined() {
        Order order = givenAPendingOrderReadyToBeProcessed();

        subscriptionService.processConfirmationForTheDay();

        assertEquals(OrderStatus.DECLINED, order.getOrderStatus());
    }

    @Test
    public void whenProcessingPendingAndReadyToProcessOrders_shouldPublishMealKitConfirmedEvent() {
        givenAConfirmedOrderReadyToBeProcessedWithMockEventBus();

        subscriptionService.processConfirmationForTheDay();

        verify(eventBus).publish(any(MealKitConfirmedEvent.class));
    }

    private Order givenAPendingOrderReadyToBeProcessed() {
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();
        SubscriptionFixture subscriptionFixture = new SubscriptionFixture();
        OrderFixture orderFixture = new OrderFixture();
        Order order = orderFixture.withOrderStatus(OrderStatus.PENDING).withDeliveryDate(LocalDate.now().plusDays(1)).build();
        Subscription subscription = subscriptionFixture.withOrders(List.of(order)).build();
        subscriptionRepository.save(subscription);
        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory =
            new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        SubscriptionFactory subscriptionFactory =
            new SubscriptionFactory(mealKitUniqueIdentifierFactory, new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class),
                new OrdersFactory(mealKitUniqueIdentifierFactory), List.of(CURRENT_SEMESTER), List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));
        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionFactory, paymentService, eventBus);
        eventBus.register(subscriptionService);

        return order;
    }

    private Order givenAConfirmedOrderReadyToBeProcessedWithMockEventBus() {
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();
        SubscriptionFixture subscriptionFixture = new SubscriptionFixture();
        OrderFixture orderFixture = new OrderFixture();
        Order order = orderFixture.withOrderStatus(OrderStatus.CONFIRMED).withDeliveryDate(LocalDate.now().plusDays(1)).build();
        Subscription subscription = subscriptionFixture.withOrders(List.of(order)).build();
        subscriptionRepository.save(subscription);
        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory =
            new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        SubscriptionFactory subscriptionFactory =
            new SubscriptionFactory(mealKitUniqueIdentifierFactory, new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class),
                new OrdersFactory(mealKitUniqueIdentifierFactory), List.of(CURRENT_SEMESTER), List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));
        eventBus = mock(RepULEventBus.class);
        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionFactory, paymentService, eventBus);
        eventBus.register(subscriptionService);

        return order;
    }
}
