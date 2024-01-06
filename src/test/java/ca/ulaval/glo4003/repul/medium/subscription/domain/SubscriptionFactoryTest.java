package ca.ulaval.glo4003.repul.medium.subscription.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrdersFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionFactoryTest {
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_UNIQUE_IDENTIFIER = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static final DayOfWeek TOMORROW_DAY_OF_WEEK = TOMORROW.getDayOfWeek();
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;

    @Test
    public void givenFactoryWithSemesterWithOnlyThisWeek_whenCreatingSubscription_shouldCreateSubscriptionOrdersForTheSemester() {
        SubscriptionFactory subscriptionFactory = createFactoryWithSemesterThisWeekOnly();

        Subscription subscription = subscriptionFactory.createSubscription(A_SUBSCRIBER_UNIQUE_IDENTIFIER, A_DELIVERY_LOCATION_ID, TOMORROW_DAY_OF_WEEK,
            A_MEALKIT_TYPE);

        assertEquals(1, subscription.getOrders().size());
        assertEquals(TOMORROW, subscription.getOrders().get(0).getDeliveryDate());
    }

    private SubscriptionFactory createFactoryWithSemesterThisWeekOnly() {
        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory =
            new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        UniqueIdentifierFactory<SubscriptionUniqueIdentifier> subscriptionUniqueIdentifierFactory =
            new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class);
        OrdersFactory ordersFactory = new OrdersFactory(mealKitUniqueIdentifierFactory);
        Semester semester = new Semester(new SemesterCode("Automne"), TOMORROW.minusDays(2), TOMORROW.plusDays(5));
        List<Semester> semesters = List.of(semester);
        return new SubscriptionFactory(mealKitUniqueIdentifierFactory, subscriptionUniqueIdentifierFactory, ordersFactory, semesters,
            List.of(DeliveryLocationId.VACHON));
    }
}
