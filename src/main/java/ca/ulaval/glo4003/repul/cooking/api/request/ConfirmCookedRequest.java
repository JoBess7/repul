package ca.ulaval.glo4003.repul.cooking.api.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfirmCookedRequest {
    @NotNull(message = "The meal kit ids may not be null.")
    public List<String> ids;
}
