package ca.ulaval.glo4003.repul.small.subscription.api.assembler;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionFixture;
import ca.ulaval.glo4003.repul.subscription.api.assembler.SubscriptionsResponseAssembler;
import ca.ulaval.glo4003.repul.subscription.api.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.domain.Frequency;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionsResponseAssemblerTest {
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final Frequency A_FREQUENCY = new Frequency(DayOfWeek.MONDAY);
    private static final LocalDate A_DATE = LocalDate.now();
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final Subscription A_SUBSCRIPTION =
        new SubscriptionFixture().withSubscriptionId(A_SUBSCRIPTION_ID).withPickupLocationId(A_DELIVERY_LOCATION_ID).withFrequency(A_FREQUENCY)
            .withMealKitType(A_MEALKIT_TYPE).withStartDate(A_DATE).build();
    private static final SubscriptionPayload SUBSCRIPTION_PAYLOAD = SubscriptionPayload.from(A_SUBSCRIPTION);
    private static final SubscriptionsPayload SUBSCRIPTIONS_PAYLOAD = new SubscriptionsPayload(List.of(SUBSCRIPTION_PAYLOAD));

    private SubscriptionsResponseAssembler subscriptionResponseAssembler;

    @Test
    public void givenSubscriptionsPayload_whenAssemblingSubscriptionsResponse_shouldReturnListOfSubscriptionResponse() {
        subscriptionResponseAssembler = new SubscriptionsResponseAssembler();

        List<SubscriptionResponse> subscriptionResponses = subscriptionResponseAssembler.toSubscriptionsResponse(SUBSCRIPTIONS_PAYLOAD);

        assertEquals(A_SUBSCRIPTION_ID.getUUID().toString(), subscriptionResponses.get(0).subscriptionId());
        assertEquals(A_DELIVERY_LOCATION_ID.toString(), subscriptionResponses.get(0).locationId());
        assertEquals(A_FREQUENCY.dayOfWeek().name(), subscriptionResponses.get(0).dayOfWeek());
        assertEquals(A_MEALKIT_TYPE.name(), subscriptionResponses.get(0).mealKitType());
        assertEquals(A_DATE.toString(), subscriptionResponses.get(0).startDate());
    }

    @Test
    public void givenSubscriptionPayload_whenAssemblingSubscriptionResponse_shouldReturnSubscriptionResponse() {
        subscriptionResponseAssembler = new SubscriptionsResponseAssembler();

        SubscriptionResponse subscriptionResponse = subscriptionResponseAssembler.toSubscriptionResponse(SUBSCRIPTION_PAYLOAD);

        assertEquals(A_SUBSCRIPTION_ID.getUUID().toString(), subscriptionResponse.subscriptionId());
        assertEquals(A_DELIVERY_LOCATION_ID.toString(), subscriptionResponse.locationId());
        assertEquals(A_FREQUENCY.dayOfWeek().name(), subscriptionResponse.dayOfWeek());
        assertEquals(A_MEALKIT_TYPE.name(), subscriptionResponse.mealKitType());
        assertEquals(A_DATE.toString(), subscriptionResponse.startDate());
    }
}
