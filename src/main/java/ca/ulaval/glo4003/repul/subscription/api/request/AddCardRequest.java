package ca.ulaval.glo4003.repul.subscription.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddCardRequest {
    @NotNull(message = "The card number may not be null.")
    public String cardNumber;
}
