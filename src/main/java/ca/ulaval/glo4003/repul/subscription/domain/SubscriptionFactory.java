package ca.ulaval.glo4003.repul.subscription.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoOrdersInDesiredPeriodException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SemesterNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrdersFactory;

public class SubscriptionFactory {
    private final UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory;
    private final UniqueIdentifierFactory<SubscriptionUniqueIdentifier> subscriptionUniqueIdentifierFactory;
    private final OrdersFactory ordersFactory;
    private final List<Semester> availableSemesters;
    private final List<DeliveryLocationId> availableDeliveryLocations;

    public SubscriptionFactory(UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory,
                               UniqueIdentifierFactory<SubscriptionUniqueIdentifier> subscriptionUniqueIdentifierFactory, OrdersFactory ordersFactory,
                               List<Semester> availableSemesters, List<DeliveryLocationId> availableDeliveryLocations) {
        this.mealKitUniqueIdentifierFactory = mealKitUniqueIdentifierFactory;
        this.subscriptionUniqueIdentifierFactory = subscriptionUniqueIdentifierFactory;
        this.ordersFactory = ordersFactory;
        this.availableSemesters = availableSemesters;
        this.availableDeliveryLocations = availableDeliveryLocations;
    }

    public Subscription createSubscription(SubscriberUniqueIdentifier subscriberId, DeliveryLocationId deliveryLocationId, DayOfWeek dayOfWeek,
                                           MealKitType mealKitType) {
        validateLocationId(deliveryLocationId);
        LocalDate today = LocalDate.now();
        Semester semester = getCurrentSemester(today);
        LocalDate semesterEndDate = semester.endDate();
        List<Order> orders = ordersFactory.createOrdersInSemester(today, semesterEndDate, dayOfWeek, mealKitType);

        return new Subscription(subscriptionUniqueIdentifierFactory.generate(), subscriberId, orders, new Frequency(dayOfWeek), deliveryLocationId, today,
            semester, mealKitType);
    }

    private void validateLocationId(DeliveryLocationId deliveryLocationId) {
        if (!availableDeliveryLocations.contains(deliveryLocationId)) {
            throw new InvalidLocationIdException();
        }
    }

    private Semester getCurrentSemester(LocalDate currentDate) {
        return availableSemesters.stream().filter(semester -> semester.startDate().isBefore(currentDate) && semester.endDate().isAfter(currentDate)).findFirst()
            .orElseThrow(SemesterNotFoundException::new);
    }

    private List<Order> createOrdersInSemester(LocalDate today, LocalDate semesterEndDate, DayOfWeek desiredDayOfWeek, MealKitType mealKitType) {
        LocalDate nextOrderDate = getFirstOrderDate(today, semesterEndDate, desiredDayOfWeek);
        List<Order> orders = new ArrayList<>();

        while (nextOrderDate.isBefore(semesterEndDate.plusDays(1))) {
            orders.add(new Order(mealKitUniqueIdentifierFactory.generate(), mealKitType, nextOrderDate));
            nextOrderDate = nextOrderDate.plusDays(7);
        }

        return orders;
    }

    private LocalDate getFirstOrderDate(LocalDate today, LocalDate semesterEndDate, DayOfWeek dayOfWeek) {
        LocalDate firstOrderDateCandidate = today;
        while (firstOrderDateCandidate.getDayOfWeek() != dayOfWeek) {
            firstOrderDateCandidate = firstOrderDateCandidate.plusDays(1);

            if (firstOrderDateCandidate.isAfter(semesterEndDate)) {
                throw new NoOrdersInDesiredPeriodException();
            }
        }
        return firstOrderDateCandidate;
    }
}
