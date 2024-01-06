package ca.ulaval.glo4003.repul.subscription.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionRequest {
    @NotNull(message = "The location Id may not be null.")
    public String locationId;

    @NotNull(message = "The day of week may not be null.")
    public String dayOfWeek;

    @NotNull(message = "The meal kit type may not be null.")
    public String mealKitType;
}
