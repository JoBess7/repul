package ca.ulaval.glo4003.repul.small.subscription.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.subscription.OrderFixture;
import ca.ulaval.glo4003.repul.subscription.domain.Frequency;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoNextOrderInSubscriptionException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeConfirmedException;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubscriptionTest {
    private static final SubscriptionUniqueIdentifier AN_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final Frequency A_FREQUENCY = new Frequency(DayOfWeek.FRIDAY);
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final Semester CURRENT_SEMESTER = new Semester(new SemesterCode("123"), LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(2));
    private static final LocalDate TODAY = LocalDate.now();

    @Test
    public void givenSubscriptionWithPendingOrderInTheFuture_whenConfirming_shouldConfirmNextOrderAndMarkItAsConfirmed() {
        Order aPendingOrderInTheFuture = new OrderFixture().withDeliveryDateIn(3).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(aPendingOrderInTheFuture), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER,
                A_MEALKIT_TYPE);

        Order confirmedOrder = subscription.confirmNextMealKit();

        Assertions.assertEquals(OrderStatus.CONFIRMED, confirmedOrder.getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithPendingOrderTooCloseInTheFuture_whenConfirming_shouldThrowOrderCannotBeConfirmedOrDeniedException() {
        Order aPendingOrderForTomorrow = new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(1)).withOrderStatus(OrderStatus.PENDING).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(aPendingOrderForTomorrow), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER,
                A_MEALKIT_TYPE);

        assertThrows(OrderCannotBeConfirmedException.class, () -> subscription.confirmNextMealKit());
    }

    @Test
    public void givenSubscriptionWithoutOrdersInTheFuture_whenConfirming_shouldThrowNoNextOrderInSubscriptionException() {
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER, A_MEALKIT_TYPE);

        assertThrows(NoNextOrderInSubscriptionException.class, () -> subscription.confirmNextMealKit());
    }

    @Test
    public void givenSubscriptionWithAlreadyDeclinedOrderInTheFuture_whenConfirming_shouldChangeStatusToConfirmed() {
        Order anOrderInTheFuture = new OrderFixture().withDeliveryDateIn(3).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(anOrderInTheFuture), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER, A_MEALKIT_TYPE);
        subscription.declineNextMealKit();

        Order confirmedOrder = subscription.confirmNextMealKit();

        Assertions.assertEquals(OrderStatus.CONFIRMED, confirmedOrder.getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithPendingOrderInTheFuture_whenDeclining_shouldDeclineNextOrderAndMarkItAsDeclined() {
        Order aPendingOrderInTheFuture = new OrderFixture().withDeliveryDateIn(3).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(aPendingOrderInTheFuture), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER,
                A_MEALKIT_TYPE);

        Order declinedOrder = subscription.declineNextMealKit();

        assertEquals(OrderStatus.DECLINED, declinedOrder.getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithoutOrdersInTheFuture_whenDeclining_shouldThrowNoNextOrderInSubscriptionException() {
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER, A_MEALKIT_TYPE);

        assertThrows(NoNextOrderInSubscriptionException.class, () -> subscription.declineNextMealKit());
    }

    @Test
    public void givenSubscriptionWithAlreadyConfirmedOrderInTheFuture_whenDeclining_shouldChangeStatusToDeclined() {
        Order anOrderInTheFuture = new OrderFixture().withDeliveryDateIn(3).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(anOrderInTheFuture), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER, A_MEALKIT_TYPE);
        subscription.confirmNextMealKit();

        Order declinedOrder = subscription.declineNextMealKit();

        Assertions.assertEquals(OrderStatus.DECLINED, declinedOrder.getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithANextOrder_whenFindingOptionalNextOrder_shouldReturnOptionalOfTheOrder() {
        Order anOrderInTheFuture = new OrderFixture().withDeliveryDateIn(3).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(anOrderInTheFuture), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER, A_MEALKIT_TYPE);

        Optional<Order> order = subscription.findCurrentOrder();

        assertEquals(anOrderInTheFuture, order.get());
    }

    @Test
    public void givenSubscriptionWithoutANextOrder_whenFindingOptionalNextOrder_shouldReturnEmptyOptional() {
        Order aPastOrder = new OrderFixture().withDeliveryDate(LocalDate.now().minusDays(2)).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(aPastOrder), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER, A_MEALKIT_TYPE);

        Optional<Order> order = subscription.findCurrentOrder();

        assertTrue(order.isEmpty());
    }

    @Test
    public void whenMarkCurrentOrderAsToCook_shouldChangeOrderStatusToToCook() {
        Order anOrder = new OrderFixture().withOrderStatus(OrderStatus.TO_DELIVER).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(anOrder), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER, A_MEALKIT_TYPE);

        subscription.markCurrentOrderAsToCook();

        assertEquals(OrderStatus.TO_COOK, anOrder.getOrderStatus());
    }

    @Test
    public void whenMarkCurrentOrderAsToDeliver_shouldChangeOrderStatusToToDeliver() {
        Order anOrder = new OrderFixture().withOrderStatus(OrderStatus.TO_COOK).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(anOrder), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER, A_MEALKIT_TYPE);

        subscription.markCurrentOrderAsToDeliver();

        assertEquals(OrderStatus.TO_DELIVER, anOrder.getOrderStatus());
    }

    @Test
    public void whenMarkCurrentOrderAsInDelivery_shouldChangeOrderStatusToInDelivery() {
        Order anOrder = new OrderFixture().withOrderStatus(OrderStatus.TO_COOK).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(anOrder), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER, A_MEALKIT_TYPE);

        subscription.markCurrentOrderAsInDelivery();

        assertEquals(OrderStatus.IN_DELIVERY, anOrder.getOrderStatus());
    }

    @Test
    public void whenMarkCurrentOrderAsToPickUp_shouldChangeOrderStatusToToPickUp() {
        Order anOrder = new OrderFixture().withOrderStatus(OrderStatus.TO_COOK).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(anOrder), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER, A_MEALKIT_TYPE);

        subscription.markCurrentOrderAsToPickUp();

        assertEquals(OrderStatus.TO_PICKUP, anOrder.getOrderStatus());
    }

    @Test
    public void whenMarkOrderAsPickedUp_shouldChangeOrderStatusToPickedUp() {
        Order anOrder = new OrderFixture().withOrderStatus(OrderStatus.TO_PICKUP).build();
        Subscription subscription =
            new Subscription(AN_ID, A_SUBSCRIBER_ID, List.of(anOrder), A_FREQUENCY, A_DELIVERY_LOCATION_ID, TODAY, CURRENT_SEMESTER, A_MEALKIT_TYPE);

        subscription.markOrderAsPickedUp(anOrder.getOrderId());

        assertEquals(OrderStatus.PICKED_UP, anOrder.getOrderStatus());
    }
}
