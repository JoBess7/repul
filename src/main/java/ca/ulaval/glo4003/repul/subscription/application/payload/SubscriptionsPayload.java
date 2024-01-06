package ca.ulaval.glo4003.repul.subscription.application.payload;

import java.util.List;

public record SubscriptionsPayload(
    List<SubscriptionPayload> subscriptions
) {
}
