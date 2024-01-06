package ca.ulaval.glo4003.repul.subscription.api.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.subscription.api.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;

public class SubscriptionsResponseAssembler {
    public List<SubscriptionResponse> toSubscriptionsResponse(SubscriptionsPayload subscriptionsPayload) {
        return subscriptionsPayload.subscriptions().stream().map(
            this::toSubscriptionResponse).toList();
    }

    public SubscriptionResponse toSubscriptionResponse(SubscriptionPayload subscriptionPayload) {
        return new SubscriptionResponse(
            subscriptionPayload.subscriptionId().getUUID().toString(),
            subscriptionPayload.frequency().dayOfWeek().name(),
            subscriptionPayload.deliveryLocationId().toString(),
            subscriptionPayload.mealKitType().name(),
            subscriptionPayload.startDate().toString(),
            subscriptionPayload.semester().semesterCode().value());
    }
}
