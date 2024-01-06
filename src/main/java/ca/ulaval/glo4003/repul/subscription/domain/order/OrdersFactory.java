package ca.ulaval.glo4003.repul.subscription.domain.order;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoOrdersInDesiredPeriodException;

public class OrdersFactory {
    UniqueIdentifierFactory<MealKitUniqueIdentifier> uniqueIdentifierFactory;

    public OrdersFactory(UniqueIdentifierFactory<MealKitUniqueIdentifier> uniqueIdentifierFactory) {
        this.uniqueIdentifierFactory = uniqueIdentifierFactory;
    }

    public List<Order> createOrdersInSemester(LocalDate today, LocalDate semesterEndDate, DayOfWeek desiredDayOfWeek, MealKitType mealKitType) {
        LocalDate nextOrderDate = getFirstOrderDate(today, semesterEndDate, desiredDayOfWeek);
        List<Order> orders = new ArrayList<>();

        while (nextOrderDate.isBefore(semesterEndDate.plusDays(1))) {
            Order order = new Order(uniqueIdentifierFactory.generate(), mealKitType, nextOrderDate);
            orders.add(order);
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
