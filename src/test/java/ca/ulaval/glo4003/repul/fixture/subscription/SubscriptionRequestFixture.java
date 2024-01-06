package ca.ulaval.glo4003.repul.fixture.subscription;

import java.time.DayOfWeek;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.subscription.api.request.SubscriptionRequest;

public class SubscriptionRequestFixture {
    private String locationId = "VACHON";
    private String dayOfWeek = DayOfWeek.MONDAY.toString();
    private String mealKitType = MealKitType.STANDARD.toString();

    public SubscriptionRequestFixture withLocationId(String locationId) {
        this.locationId = locationId;
        return this;
    }

    public SubscriptionRequestFixture withDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public SubscriptionRequestFixture withMealKitType(String mealKitType) {
        this.mealKitType = mealKitType;
        return this;
    }

    public SubscriptionRequest build() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.locationId = locationId;
        subscriptionRequest.dayOfWeek = dayOfWeek;
        subscriptionRequest.mealKitType = mealKitType;
        return subscriptionRequest;
    }
}
