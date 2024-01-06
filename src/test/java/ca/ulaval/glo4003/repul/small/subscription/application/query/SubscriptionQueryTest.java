package ca.ulaval.glo4003.repul.small.subscription.application.query;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidDayOfWeekException;
import ca.ulaval.glo4003.repul.subscription.application.query.SubscriptionQuery;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubscriptionQueryTest {
    private static final String A_DELIVERY_LOCATION_ID = "123";
    private static final String AN_INVALID_DAY_OF_WEEK = "SATURUNDAY";
    private static final String A_MEAL_KIT_TYPE = "STANDARD";

    @Test
    public void givenInvalidDayOfWeek_whenCreating_thenThrowInvalidDayOfWeekException() {
        assertThrows(InvalidDayOfWeekException.class, () -> SubscriptionQuery.from(A_DELIVERY_LOCATION_ID, AN_INVALID_DAY_OF_WEEK, A_MEAL_KIT_TYPE));
    }
}
